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
    category = ndb.StringProperty(choices=('food', 'drink', 'ride', 'delivery', 'study'))

    task_location = ndb.StringProperty()
    final_dest = ndb.StringProperty()

    time_created = ndb.DateTimeProperty(auto_now_add=True)
    last_update = ndb.DateTimeProperty(auto_now=True)

    expiration_time = ndb.DateTimeProperty(auto_now=True)
    credit = ndb.IntegerProperty()
    status = ndb.StringProperty(choices=('drafting', 'posted', 'completed', 'inactive'))



