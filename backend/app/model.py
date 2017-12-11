from math import radians, sin, cos, asin, sqrt

from google.appengine.ext import ndb


class User(ndb.Model):
    user_email = ndb.StringProperty()
    user_name = ndb.StringProperty()
    display_name = ndb.StringProperty()
    rating = ndb.IntegerProperty()
    ratings_all = ndb.IntegerProperty(repeated=True)
    credit = ndb.IntegerProperty()
    profile_image = ndb.BlobProperty()


class Task(ndb.Model):
    owner_email = ndb.StringProperty()
    helper = ndb.StringProperty()
    #helpee is the one who is helped by helper
    helpee = ndb.StringProperty()

    title = ndb.StringProperty()
    description = ndb.StringProperty()
    type = ndb.StringProperty(choices=['provide_help', 'seek_help'])
    category = ndb.StringProperty(choices=['Food', 'Drink', 'Ride', 'Delivery', 'Study', 'Other'])

    task_location = ndb.StringProperty()
    final_dest = ndb.StringProperty()

    time_created = ndb.DateTimeProperty(auto_now_add=True)
    last_update = ndb.DateTimeProperty(auto_now=True)

    expiration_time = ndb.DateTimeProperty(auto_now=True)
    credit = ndb.IntegerProperty()
    status = ndb.StringProperty(choices=['Drafting', 'Posted', 'Completed', 'Ongoing', 'Deleted'])

    #location info
    latitude = ndb.FloatProperty()
    longitude = ndb.FloatProperty()




def post_a_task(task_latitude, task_longitude, task_title, task_category, task_type, task_detail,
                task_location, desitination_location, task_status,
                task_onwer, extra_credit, expiration_time = 3600):
    new_task = Task(latitude=float(task_latitude), longitude=float(task_longitude), title=task_title, category=task_category, type=task_type, description=task_detail,
                    task_location=task_location, final_dest=desitination_location, status=task_status,
                    owner_email=task_onwer, credit=10)
    new_task.put()
    return 0


def get_all_tasks():
    all_tasks = Task.query()
    return all_tasks


def get_tasks_by_status(status='Posted'):
    filter_tasks = Task.query(Task.status == status)
    return filter_tasks


def get_user(user_email):
    user = User.query(User.user_email == user_email)
    return user.fetch()


def get_tasks_by_type(task_type):
    filter_task = Task.query(Task.type == task_type)
    return filter_task.fetch()


def get_tasks_by_email(owner_email):
    filter_tasks = Task.query(Task.owner_email == owner_email)
    return filter_tasks


def get_icon(owner_email):
    user = get_user_by_email(owner_email)
    if user:
        return user.profile_image


def get_task(owner_email, task_title):
    filter_tasks = Task.query(Task.owner_email == owner_email, Task.title == task_title)
    return filter_tasks.get()


def get_task_by_id(task_id):
    filter_task = Task.get_by_id(int(task_id))
    return filter_task


def get_name_by_email(user_email):
    user = get_user_by_email(user_email)
    if user:
        return user.display_name


def add_user(user_email, user_name):
    user = get_user(user_email)
    if not user:
        user = User(user_name=user_name, user_email=user_email)


def delete_task(owner_email, task_id):
    task = get_task_by_id(task_id)
    if task.owner_email == owner_email:
        task.status = "Deleted"
        task.put()
        return 0
    else:
        return 1


def update_task(owner_email, task_title, requestee, status):
    task = get_task(owner_email, task_title)
    if task:
        if status == 'Posted':
            task.status = 'Ongoing'
        elif status == 'Pending':
            task.status = 'Completed'

        if task.type == 'provide_help':
            task.helpee = requestee
        elif task.type == 'need_help':
            task.helper = requestee
        task.put()


def get_user_by_email(user_email):
    user = User.query(User.user_email == user_email)
    return user.get()


def create_user(user_latitude, user_longitude, user_email, user_name, profile_image):
    check_existing = get_user_by_email(user_email)
    if check_existing:
        return 1
    else:
        new_user = User(user_email=user_email, user_name=user_name,
                        display_name=user_name, profile_image=profile_image,
                        credit=50, rating=5, ratings_all=[5])
        new_user.put()
        return 0


def update_profile(user_email, display_name, profile_image):
    user = get_user_by_email(user_email)
    user.display_name = display_name
    user.profile_image = profile_image
    user.put()
    return 0

def get_nearby_task (latitude, longitude, start, count):
    tasks = Task.query().fetch()
    sorted(tasks, key=lambda task: get_distance(longitude, latitude, tasks.longitude, tasks.latitude))
    return tasks[start:(start + count)]

def get_distance(lon1, lat1, lon2, lat2):
    """
    Calculate the great circle distance between two points
    on the earth
    """
    # convert decimal degrees to radians
    lon1, lat1, lon2, lat2 = map(radians, [lon1, lat1, lon2, lat2])
    # haversine formula
    dlon = lon2 - lon1
    dlat = lat2 - lat1
    a = sin(dlat/2)**2 + cos(lat1) * cos(lat2) * sin(dlon/2)**2
    c = 2 * asin(sqrt(a))
    # Radius of earth in kilometers is 6371
    km = 6371* c
    return km

