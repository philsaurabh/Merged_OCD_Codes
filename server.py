
from flask import Flask, render_template
import pymongo

app = Flask(__name__)

#Databases
client = pymongo.MongoClient('localhost', 27017)
db = client.user_login_system


#routes
# from user import routes
from user.models import User


#########################
@app.route("/user/signup",methods=['POST'])
def signup():
    return User().signup()

@app.route("/")
def home():
    return  render_template("index.html")

    # return "Hello, World!"

@app.route("/dashboard")
def dashboard():
    return render_template("dashboard.html")




#########################
if __name__=="__main__":
    app.run(debug=True)