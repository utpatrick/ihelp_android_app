from google.appengine.ext import ndb


class User(ndb.Model):
    user_email = ndb.StringProperty()
    user_name = ndb.StringProperty()
    ratings = ndb.IntegerProperty(repeated=True)
    credit = ndb.IntegerProperty()
    profile_image = ndb.BlobProperty()


class Task(ndb.Model):
    owner_email = ndb.StringProperty()
    helper = ndb.StringProperty()
    #helpee is the one who is helped by helper
    helpee = ndb.StringProperty(repeated=True)

    title = ndb.StringProperty()
    description = ndb.StringProperty()
    type = ndb.StringProperty(choices=('provide_help', 'seek_help'))
    category = ndb.StringProperty(choices=('Food', 'Drink', 'Ride', 'Delivery', 'Study', 'Other'))

    task_location = ndb.StringProperty()
    final_dest = ndb.StringProperty()

    time_created = ndb.DateTimeProperty(auto_now_add=True)
    last_update = ndb.DateTimeProperty(auto_now=True)

    expiration_time = ndb.DateTimeProperty(auto_now=True)
    credit = ndb.IntegerProperty()
    status = ndb.StringProperty(choices=('drafting', 'posted', 'completed', 'inactive'))


def post_a_task(task_title, task_category, task_type, task_detail,
                task_location, desitination_location, task_status,
                task_onwer, extra_credit, expiration_time = 3600):
    new_task = Task(title=task_title, category=task_category, type=task_type, description=task_detail,
                    task_location=task_location, final_dest=desitination_location, status=task_status,
                    owner_email=task_onwer, credit=10)
    new_task.put()
    return 0


def get_all_tasks():
    all_tasks = Task.query()
    return all_tasks


def get_tasks_by_status(status='posted'):
    filter_tasks = Task.query(Task.status == status)
    return filter_tasks