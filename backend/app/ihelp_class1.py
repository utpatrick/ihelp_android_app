# [START imports]
import os
import app.model
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

IMAGE_COUNT = 16


class GetAllStreams(webapp2.RequestHandler):
    def get(self):
        orig_streams = model.get_all_stream()
        streams = sorted(orig_streams, key=lambda x: x.last_update, reverse=True)
        response_content = [{'stream_name': s.stream_name, 'cover_image': s.cover_image} for s in streams]
        self.response.headers['Content-Type'] = 'application/json'
        self.response.out.write(json.dumps(response_content))


# [START app]
app = webapp2.WSGIApplication([
    ('/android/view_all_streams', GetAllStreams)
], debug=True)
# [END app]
