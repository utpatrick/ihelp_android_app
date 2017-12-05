# [START imports]
import os
import model
import json

import jinja2
import webapp2

from google.appengine.api import users
from google.appengine.ext import blobstore
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.ext import ndb

JINJA_ENVIRONMENT = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__)),
    extensions=['jinja2.ext.autoescape'],
    autoescape=True)
# [END imports]


class PostATask(webapp2.RequestHandler):
    def post(self):
        task_title = self.request.get('task_title')
        task_category = self.request.get('task_category')
        task_type = self.request.get('task_type')
        task_detail = self.request.get('task_detail')
        task_location = self.request.get('task_location')
        desitination_location = self.request.get('desitination_location')
        task_status = self.request.get('task_status')
        task_onwer = self.request.get('task_onwer')
        extra_credit = self.request.get('extra_credit')
        owner_name = self.request.get('owner_name')
        #expiration_time = self.request.get('expiration_time')

        post_status = model.post_a_task(task_title, task_category, task_type, task_detail,
                                        task_location, desitination_location, task_status,
                                        task_onwer, extra_credit)
        if post_status == 0:

            response_content = {'status': 'ok',}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class GetAllTasks(webapp2.RequestHandler):
    def get(self):
        all_tasks = model.get_tasks_by_status()
        sorted_task = sorted(all_tasks, key=lambda x: x.last_update, reverse=True)
        response_content = [{'task_title': s.title, 'task_category': s.category, 'task_type': s.type,
                             'task_detail': s.description, 'task_location': s.task_location,
                             'desitination_location': s.final_dest, 'task_status': s.status,
                             'task_onwer': s.owner_email, 'extra_credit': s.credit} for s in sorted_task]
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class UpdateProfile(blobstore_handlers.BlobstoreUploadHandler):
    def post(self):
        stream_name = self.request.get('stream')
        user_email = self.request.get('user_email')
        user_id = model.user_email_to_user_id(user_email)
        title = self.request.get('title')
        content = self.get_uploads()[0]
        lat = self.request.get('lat')
        long = self.request.get('long')
        tags = self.request.get('tags')
        geo_info = ndb.GeoPt(float(lat), float(long))
        model.add_photo_geo(user_id, stream_name, title, content.key(), geo_info, tags)
        if content:
            response_content = {'status': 'ok',
                                'lat': lat,
                                'longG': long}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))

    def get(self):
        upload_url = blobstore.create_upload_url('/android/upload_image')
        self.response.headers['Content-Type'] = 'application/json'
        response_content = {'uploadURL': upload_url}
        self.response.out.write(json.dumps(response_content))


class ICanHelp(webapp2.RequestHandler):
    def get(self):
        category = self.request.get('category')
        tasks = model.get_tasks_by_type('seek_help')
        sorted_task = sorted(tasks, key=lambda x: x.last_update, reverse=True)
        response_content = []
        for task in sorted_task:
            owner = task.owner_email
            profile_image = model.get_icon(owner)
            if (not category or task.category == category) and task.status != 'completed':
                response_content.append({'task_title': task.title,
                                         'task_detail': task.description,
                                         'task_owner': task.owner_email,
                                         'icon': profile_image})
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class INeedHelp(webapp2.RequestHandler):
    def get(self):
        category = self.request.get('category')
        tasks = model.get_tasks_by_type('provide_help')
        sorted_task = sorted(tasks, key=lambda x: x.last_update, reverse=True)
        response_content = []
        for task in sorted_task:
            owner = task.owner_email
            profile_image = model.get_icon(owner)
            if (not category or task.category == category) and task.status != 'completed':
                response_content.append({'task_title': task.title,
                                         'task_detail': task.description,
                                         'task_owner': task.owner_email,
                                         'icon': profile_image})
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class ViewTask(webapp2.RequestHandler):
    def get(self):
        owner_email = self.request.get('task_owner')
        task_title = self.request.get('task_title')
        task = model.get_task(owner_email, task_title)
        owner_name = model.get_name_by_email(owner_email)
        if task:
            response_content = {'task_title': task.title,
                                'task_category': task.category,
                                'task_type': task.type,
                                'task_detail': task.description,
                                'task_location': task.task_location,
                                'destination': task.final_dest,
                                'task_status': task.status,
                                'task_owner': task.owner_email,
                                'owner_name': owner_name,
                                'extra_credit': task.credit}
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(response_content))


class GetIcon(webapp2.RequestHandler):
    def get(self):
        owner_email = self.request.get('owner_email')
        if owner_email:
            icon = model.get_icon(owner_email)
            self.response.out.write(icon)


class ChangeStatus(webapp2.RequestHandler):
    def get(self):
        owner = self.request.get('owner')
        task_title = self.request.get('task_title')
        requestee = self.request.get('requestee')
        status = self.request.get('status')
        model.update_task(owner, task_title, requestee, status)

# [START app]
app = webapp2.WSGIApplication([
    ('/android/post_a_task', PostATask),
    ('/android/get_all_task', GetAllTasks),
    ('/android/update_profile', UpdateProfile),
    ('/android/get_icon', GetIcon),
    ('/android/i_can_help', ICanHelp),
    ('/android/i_need_help', INeedHelp),
    ('/android/view_task', ViewTask),
    ('/android/change_status', ChangeStatus)
], debug=True)
# [END app]
