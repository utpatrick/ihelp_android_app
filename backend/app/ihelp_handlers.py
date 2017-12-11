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

            response_content = {'status': 'ok'}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class GetAllTasks(webapp2.RequestHandler):
    def get(self):
        owner_email = self.request.get('owner_email')
        all_tasks = model.get_tasks_by_status()
        sorted_task = sorted(all_tasks, key=lambda x: x.last_update, reverse=True)
        response_content = [{'task_title': s.title,
                             'task_category': s.category,
                             'task_type': s.type,
                             'task_detail': s.description,
                             'task_location': s.task_location,
                             'desitination_location': s.final_dest,
                             'task_status': s.status,
                             'task_onwer': s.owner_email,
                             'extra_credit': s.credit,
                             'task_id': s.key.id()} for s in sorted_task]
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class ManageTasks(webapp2.RequestHandler):
    def get(self):
        owner_email = self.request.get('owner_email')
        task_status = self.request.get('task_status')
        all_tasks = model.get_task_for_manage(owner_email)
        sorted_task = sorted(all_tasks, key=lambda x: x.last_update, reverse=True)
        response_content = []
        for task in sorted_task:
            owner = task.owner_email
            if (not task_status or task.status == task_status) and task.status != 'Deleted':
                response_content.append({'task_title': task.title,
                                         'task_detail': task.description,
                                         'task_owner': task.owner_email,
                                         'task_status': task.status,
                                         'task_category': task.category,
                                         'task_id': task.key.id()})
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class CreateUser(webapp2.RequestHandler):
    def post(self):
        user_email = self.request.get('user_email')
        user_name = self.request.get('user_name')
        profile_image = self.request.get('profile_image')
        create_status = model.create_user(user_email, user_name, profile_image)
        if create_status == 0:
            response_content = {'status': 'ok'}
        elif create_status == 1:
            response_content = {'status': 'existed'}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class UpdateProfile(webapp2.RequestHandler):
    def post(self):
        user_email = self.request.get('user_email')
        display_name = self.request.get('display_name')
        profile_image = self.request.get('profile_image')
        update_status = model.update_profile(user_email, display_name, profile_image)
        if update_status == 0:
            response_content = {'status': 'ok'}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))

    def get(self):
        user_email = self.request.get('user_email')
        user = model.get_user_by_email(user_email)
        self.response.headers['Content-Type'] = 'application/json'
        response_content = {'display_name': user.display_name,
                            'credit': user.credit, 'rating': user.rating}
        self.response.out.write(json.dumps(response_content))


class ProfileImage(webapp2.RequestHandler):
    def get(self):
        user_email = self.request.get('user_email')
        user = model.get_user_by_email(user_email)
        if user:
            self.response.headers['Content-Type'] = 'image/png'
            self.response.out.write(user.profile_image)
        else:
            self.response.out.write('No image')


class ICanHelp(webapp2.RequestHandler):
    def get(self):
        category = self.request.get('category')
        tasks = model.get_tasks_by_type('seek_help')
        sorted_task = sorted(tasks, key=lambda x: x.last_update, reverse=True)
        response_content = []
        main_category = {'Food', 'Drink', 'Ride'}
        for task in sorted_task:
            if category == 'Other':
                if task.category not in main_category and task.status == 'Posted':
                    response_content.append({'task_title': task.title,
                                             'task_detail': task.description,
                                             'task_owner': task.owner_email,
                                             'task_category': task.category,
                                             'task_id': task.key.id()})
            else:
                if (not category or task.category == category) and task.status == 'Posted':
                    response_content.append({'task_title': task.title,
                                             'task_detail': task.description,
                                             'task_owner': task.owner_email,
                                             'task_category': task.category,
                                             'task_id': task.key.id()})
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class INeedHelp(webapp2.RequestHandler):
    def get(self):
        category = self.request.get('category')
        tasks = model.get_tasks_by_type('provide_help')
        sorted_task = sorted(tasks, key=lambda x: x.last_update, reverse=True)
        response_content = []
        main_category = {'Food', 'Drink', 'Ride'}
        for task in sorted_task:
            if category == 'Other':
                if task.category not in main_category and task.status == 'Posted':
                    response_content.append({'task_title': task.title,
                                             'task_detail': task.description,
                                             'task_owner': task.owner_email,
                                             'task_category': task.category,
                                             'task_id': task.key.id()})
            else:
                if (not category or task.category == category) and task.status == 'Posted':
                    response_content.append({'task_title': task.title,
                                             'task_detail': task.description,
                                             'task_owner': task.owner_email,
                                             'task_category': task.category,
                                             'task_id': task.key.id()})
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class ViewTask(webapp2.RequestHandler):
    def get(self):
        owner_email = self.request.get('task_owner')
        task_title = self.request.get('task_title')
        task = model.get_task(owner_email, task_title)
        owner_display_name = model.get_name_by_email(owner_email)
        owner_rating = model.get_rating(owner_email)
        if task:
            response_content = {'task_title': task.title,
                                'task_category': task.category,
                                'task_type': task.type,
                                'task_detail': task.description,
                                'task_location': task.task_location,
                                'destination': task.final_dest,
                                'task_status': task.status,
                                'task_owner': task.owner_email,
                                'owner_name': owner_display_name,
                                'extra_credit': task.credit,
                                'rating': owner_rating,
                                'task_id': task.key.id(),
                                'helper_email':task.helper,
                                'helpee_email':task.helpee}
            self.response.headers['Content-Type'] = 'application/json'
            self.response.out.write(json.dumps(response_content))


class GetIcon(webapp2.RequestHandler):
    def get(self):
        owner_email = self.request.get('owner_email')
        if owner_email:
            icon = model.get_icon(owner_email)
            self.response.out.write(icon)


class DeleteTask(webapp2.RequestHandler):
    def post(self):
        task_id = self.request.get('task_id')
        owner_email = self.request.get('owner_email')
        post_status = model.delete_task(owner_email, task_id)
        if post_status == 0:
            response_content = {'status': 'ok'}
        elif post_status == 1:
            response_content = {'status': 'invalid'}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))

    def get(self):
        task_id = self.request.get('task_id')
        task = model.get_task_by_id(task_id)
        if task:
            response_content = {'status': 'ok'}
        else:
            response_content = {'status': 'no'}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


class ChangeStatus(webapp2.RequestHandler):
    def get(self):
        owner = self.request.get('owner')
        task_title = self.request.get('task_title')
        requestee = self.request.get('requestee')
        status = self.request.get('status')
        rating = self.request.get('rating')
        model.update_task(owner, task_title, requestee, status)
        if rating:
            model.update_rating(owner, rating)


class GetRating(webapp2.RequestHandler):
    def get(self):
        owner = self.request.get('user_email')
        response_content = {'rating': model.get_rating(owner)}
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))



# [START app]
app = webapp2.WSGIApplication([
    ('/android/post_a_task', PostATask),
    ('/android/get_all_task', GetAllTasks),
    ('/android/manage_task', ManageTasks),
    ('/android/create_user', CreateUser),
    ('/android/update_profile', UpdateProfile),
    ('/android/get_icon', GetIcon),
    ('/android/i_can_help', ICanHelp),
    ('/android/i_need_help', INeedHelp),
    ('/android/view_task', ViewTask),
    ('/android/change_status', ChangeStatus),
    ('/android/profile_image', ProfileImage),
    ('/android/change_status', ChangeStatus),
    ('/android/get_rating', GetRating),
    ('/android/delete_task', DeleteTask)
], debug=True)
# [END app]
