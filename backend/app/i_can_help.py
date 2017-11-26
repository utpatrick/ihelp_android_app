# [START imports]
import os
import app.model
import json

import jinja2
import webapp2

JINJA_ENVIRONMENT = jinja2.Environment(
    loader=jinja2.FileSystemLoader(os.path.dirname(__file__)),
    extensions=['jinja2.ext.autoescape'],
    autoescape=True)
# [END imports]


class ICanHelp(webapp2.RequestHandler):
    def get(self):
        self.response.out.write("hello world!")


# [START app]
app = webapp2.WSGIApplication([
    ('/icanhelp', ICanHelp)
], debug=True)
# [END app]
