from flask import Flask
from server import app
from user.models import User

@app.route("/user/signup/", methods=['GET'])
def signup():
  return User().signup()