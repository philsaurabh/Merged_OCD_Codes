# Importing libraries and warning settings
from datetime import datetime, date
from multiprocessing import Process
import threading
import warnings
import pyttsx3
import io
from PIL import Image
from pymongo import MongoClient
from pygame import mixer
import pandas as pd
import sys
import time
import numpy as np
from tensorflow.keras.preprocessing.image import img_to_array
import tensorflow as tf  # version 1.14
import cv2
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
warnings.filterwarnings('ignore')

# folder
path = "c:\\Users\\asus\\Desktop\\ocd\\ocd_module_deployment\\record"

# Check whether the specified path exists or not
isExist = os.path.exists(path)
print(isExist)

if not isExist:
    os.makedirs(path)
    print("new directory is created!")

if isExist:
    path_w = "c:\\Users\\asus\\Desktop\\ocd\\ocd_module_deployment\\record\\weapon"    
    isExist = os.path.exists(path_w)
    print(isExist)

    if not isExist:
        os.makedirs(path_w)
        print("new directory is created!")

    path_v = "c:\\Users\\asus\\Desktop\\ocd\\ocd_module_deployment\\record\\vehicle"    
    isExist = os.path.exists(path_v)
    print(isExist)

    if not isExist:
        os.makedirs(path_v)
        print("new directory is created!")   

    path_f = "c:\\Users\\asus\\Desktop\\ocd\\ocd_module_deployment\\record\\face"    
    isExist = os.path.exists(path_f)
    print(isExist)

    if not isExist:
        os.makedirs(path_f)
        print("new directory is created!")   

    path_fb = "c:\\Users\\asus\\Desktop\\ocd\\ocd_module_deployment\\record\\fullbody"    
    isExist = os.path.exists(path_fb)
    print(isExist)

    if not isExist:
        os.makedirs(path_fb)
        print("new directory is created!")      

# Timing
now = datetime.now()
start_time = 10
t3 = now.strftime("%Hh%Mm%Ss")
today = date.today()
d3 = today.strftime("%B%d%Y")

# Voice Alert initialization
'''
engine = pyttsx3.init()
voices = engine.getProperty('voices')
engine.setProperty('voice', voices[1].id)
# engine.setProperty('rate', )
'''


def textToSpeech():
    engine = pyttsx3.init()
    voices = engine.getProperty("voices")
    engine.setProperty("voice", voices[1].id)
    engine.setProperty("rate", 220)
    engine.say("Suspecious Activity Detected!")
    engine.runAndWait()
    del engine


def alert():
    t = threading.Thread(target=textToSpeech)
    t.start()


'''
# Audio Alert
def alert():
    mixer.init()
    mixer.music.load("beep.mp3")
    mixer.music.play()
    time.sleep(1)
    mixer.music.stop()
'''
client = MongoClient("mongodb://localhost:27017")
db = client.edgedev
oproof = db.oproof
faceproof = db.faceproof
vehicleproof = db.vehicleproof
weaponproof = db.weaponproof
fullbodyproof = db.fullbodyproof
sproof = db.sproof
wproof = db.wproof

# Prediction mechanism obscene
string_pred_gen = ['Obscene', 'NonObscene']
interpreter_obs = tf.lite.Interpreter(model_path="obscene.tflite")
interpreter_obs.allocate_tensors()
input_details_obs = interpreter_obs.get_input_details()
output_details_obs = interpreter_obs.get_output_details()
input_shape_obs = input_details_obs[0]['shape']

# Prediction mechanism scuffle
string_pred_gena = ['NoScuffle', 'Scuffle']
interpreter_scuf = tf.lite.Interpreter(model_path="obscene.tflite")
interpreter_scuf.allocate_tensors()
input_details_scuf = interpreter_scuf.get_input_details()
output_details_scuf = interpreter_scuf.get_output_details()
input_shape_scuf = input_details_scuf[0]['shape']
'''
# Prediction mechanism weapons
net = cv2.dnn.readNet("yolov3_training_2000.weights", "yolov3_testing.cfg")
classes = ["Weapon"]
layer_names = net.getLayerNames()
output_layers = [layer_names[i[0] - 1] for i in net.getUnconnectedOutLayers()]
'''
# Initializing variables
input_im = None
Time = []
Date = []
Act = []
count = 0
# colors = np.random.uniform(0, 255, size=(len(classes), 3))
act = "act"
# reading the stream and resizing it from (320x240, 640x480, 1280x720, 1920x1080)
# "http://192.168.0.2:4747/video")#"dems.mp4"http://192.168.0.2:4747/video or 0,1,2
cap = cv2.VideoCapture("knifeVideo.mp4")
cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
font = cv2.FONT_HERSHEY_TRIPLEX
if (cap.isOpened() == False):
    # print("Error reading video file")
    engine = pyttsx3.init()
    engine.say(
        "Your CCTV camera is not working or turned off! Please check it and get back here!")
    engine.runAndWait()
    sys.exit()
frame_width = int(cap.get(3))
frame_height = int(cap.get(4))
size = (frame_width, frame_height)

while True:
    _, frame = cap.read()
    input_im = frame
    count = 0
    start_time = 10
    now = datetime.now()
    d3 = today.strftime("%B %d, %Y")
    t3 = now.strftime("%H:%M:%S")
    cv2.putText(input_im, d3, (20, 400), font, 1,
                (250, 250, 250), 1, cv2.LINE_AA)
    cv2.putText(input_im, t3, (20, 440), font, 1,
                (250, 250, 250), 1, cv2.LINE_AA)

    input_im = cv2.resize(input_im, (320, 320))
    input_im = img_to_array(input_im)
    input_im = np.expand_dims(input_im, axis=0)
    height, width, channels = frame.shape
    '''
    blob = cv2.dnn.blobFromImage(
        frame, 0.00392, (416, 416), (0, 0, 0), True, crop=False)
    net.setInput(blob)
    outs = net.forward(output_layers)
    class_ids = []
    confidences = []
    boxes = []
    for out in outs:
        for detection in out:
            scores = detection[5:]
            class_id = np.argmax(scores)
            confidence = scores[class_id]
            if confidence > 0.5:
                # Object detected
                center_x = int(detection[0] * width)
                center_y = int(detection[1] * height)
                w = int(detection[2] * width)
                h = int(detection[3] * height)

                # Rectangle coordinates
                x = int(center_x - w / 2)
                y = int(center_y - h / 2)

                boxes.append([x, y, w, h])
                confidences.append(float(confidence))
                class_ids.append(class_id)
    indexes = cv2.dnn.NMSBoxes(boxes, confidences, 0.5, 0.4)
    '''
    # Predict
    input_data = np.array(input_im)
    interpreter_obs.set_tensor(input_details_obs[0]['index'], input_data)
    interpreter_obs.invoke()
    interpreter_scuf.set_tensor(input_details_scuf[0]['index'], input_data)
    interpreter_scuf.invoke()
    output_data_obs = interpreter_obs.get_tensor(
        output_details_obs[0]['index'])
    index_pred_obs = 1 if (output_data_obs[0][1] > 0.5) else 0
    output_data_scuf = interpreter_scuf.get_tensor(
        output_details_scuf[0]['index'])
    ind = 1 if (output_data_scuf[0][1] > 0.75) else 0
    prezic_obs = string_pred_gen[index_pred_obs]
    prezic_scuf = string_pred_gena[ind]
    d3 = today.strftime("%B%d%Y")
    t3 = now.strftime("%Hh%Mm%Ss")

    if(prezic_obs == 'Obscene') and (prezic_scuf == 'Scuffle'):
        cv2.putText(frame, prezic_obs, (40, 50), font,
                    2, (0, 0, 255), 5, cv2.LINE_AA)
        cv2.putText(frame, prezic_scuf, (40, 100), font,
                    2, (0, 0, 255), 5, cv2.LINE_AA)
        alert()
        act = "Obscene and Scuffle"
        OSFileName = "record/obsProof/obs_" + str(d3) + str(t3) + ".jpg"
        cv2.imwrite(OSFileName, frame)
        OSFileName = "record/scfProof/scf_" + str(d3) + str(t3) + ".jpg"
        cv2.imwrite(OSFileName, frame)
        ima = Image.open(OSFileName)
        image_bytes = io.BytesIO()
        ima.save(image_bytes, format='JPEG')
        image = {
            'data': image_bytes.getvalue(),
            'Suspecious Activity': act,
            'date': d3,
            'time': t3
        }
        oproof.insert_one(image)
        sproof.insert_one(image)
    elif (prezic_obs == 'Obscene') and (prezic_scuf != 'Scuffle'):
        cv2.putText(frame, prezic_obs, (40, 50), font,
                    2, (0, 0, 255), 5, cv2.LINE_AA)
        cv2.putText(frame, prezic_scuf, (40, 100), font,
                    2, (255, 255, 255), 1, cv2.LINE_AA)
        alert()
        act = "Obscene"
        OFileName = "record/obsProof/obs_" + str(d3) + str(t3) + ".jpg"
        cv2.imwrite(OFileName, frame)
        ima = Image.open(OFileName)
        image_bytes = io.BytesIO()
        ima.save(image_bytes, format='JPEG')
        image = {
            'data': image_bytes.getvalue(),
            'Suspecious Activity': act,
            'date': d3,
            'time': t3
        }
        oproof.insert_one(image)
    elif (prezic_obs != 'Obscene') and (prezic_scuf == 'Scuffle'):
        cv2.putText(frame, prezic_obs, (40, 50), font,
                    2, (255, 255, 255), 1, cv2.LINE_AA)
        cv2.putText(frame, prezic_scuf, (40, 100), font,
                    2, (0, 0, 255), 5, cv2.LINE_AA)
        alert()
        act = "Scuffle"
        SFileName = "record/scfProof/scf_" + str(d3) + str(t3) + ".jpg"
        cv2.imwrite(SFileName, frame)
        ima = Image.open(SFileName)
        image_bytes = io.BytesIO()
        ima.save(image_bytes, format='JPEG')
        image = {
            'data': image_bytes.getvalue(),
            'Suspecious Activity': act,
            'date': d3,
            'time': t3
        }
        sproof.insert_one(image)
    else:
        cv2.putText(frame, prezic_obs, (20, 30), font,
                    1, (255, 255, 255), 1, cv2.LINE_AA)
        cv2.putText(frame, prezic_scuf, (20, 60), font,
                    1, (255, 255, 255), 1, cv2.LINE_AA)
    '''
    if indexes == 0:
        for i in range(len(boxes)):
            if i in indexes:
                x, y, w, h = boxes[i]
                label = str(classes[class_ids[i]])
                color = colors[class_ids[i]]
                cv2.rectangle(frame, (x, y), (x + w, y + h), color, 2)
                cv2.putText(frame, label, (x, y + 20), font, 1, color, 5)
        alert()
        act ="Weapon caught"
        WFileName = "record/armsProof/arms_" + str(d3) + str(t3) + ".jpg"
        cv2.imwrite(WFileName, frame)
        ima=Image.open(WFileName)
        image_bytes = io.BytesIO()
        ima.save(image_bytes, format='JPEG')
        image = {
            'data': image_bytes.getvalue(),
            'Suspecious Activity': act,
            'date': d3,
            'time': t3
        }
        wproof.insert_one(image)
    '''
    if(prezic_obs == 'Obscene') or (prezic_scuf == 'Scuffle'):  # or (indexes == 0):
        Time.append(t3)
        Date.append(d3)
        Act.append(act)
        dict = {'Time': Time, 'Date': Date, 'Activity': Act}
        df = pd.DataFrame(dict)
        df.to_csv('record/record.csv')
        df.to_excel('record/recording.xlsx')
        detector = cv2.CascadeClassifier('haarcascade_frontalface_default.xml')
        faces = detector.detectMultiScale(frame, 1.3, 5, 0)
        for (ex, ey, ew, eh) in faces:
            roi_color = frame[ey:ey+eh, ex:ex+ew]
            cv2.rectangle(frame, (ex, ey), (ex+ew, ey+eh), (0, 255, 0), 2)
            FFileName = "record/face/faces_" + str(d3) + str(t3) + ".jpg"
            cv2.imwrite(FFileName, roi_color)
            ima = Image.open(FFileName)
            image_bytes = io.BytesIO()
            ima.save(image_bytes, format='JPEG')
            image = {
                'data': image_bytes.getvalue(),
                'Suspecious Activity': act,
                'date': d3,
                'time': t3
            }
            faceproof.insert_one(image)

# weapon
        w_detector = cv2.CascadeClassifier('w_cascade.xml')
        weapons = detector.detectMultiScale(frame, 1.3, 5, 0)
        for (ex, ey, ew, eh) in weapons:
            roi_color = frame[ey:ey+eh, ex:ex+ew]
            cv2.rectangle(frame, (ex, ey), (ex+ew, ey+eh), (0, 255, 0), 2)
            WFileName = "record/weapon/weapons_" + str(d3) + str(t3) + ".jpg"
            cv2.imwrite(WFileName, roi_color)
            ima = Image.open(WFileName)
            image_bytes = io.BytesIO()
            ima.save(image_bytes, format='JPEG')
            image = {
                'data': image_bytes.getvalue(),
                'Suspecious Activity': act,
                'date': d3,
                'time': t3
            }
            weaponproof.insert_one(image)

        v_detector=cv2.CascadeClassifier('v_cascade.xml')
        vehicles=detector.detectMultiScale(frame,1.3, 5, 0)
        for (ex, ey, ew, eh) in vehicles:
            roi_color = frame[ey:ey+eh, ex:ex+ew]
            cv2.rectangle(frame, (ex, ey), (ex+ew, ey+eh), (0, 255, 0), 2)
            VFileName = "record/vehicle/vehicles_" + str(d3) + str(t3) + ".jpg"
            cv2.imwrite(VFileName, roi_color)
            ima=Image.open(VFileName)
            image_bytes = io.BytesIO()
            ima.save(image_bytes, format='JPEG')
            image = {
                'data': image_bytes.getvalue(),
                'Suspecious Activity': act,
                'date': d3,
                'time': t3
            }
            vehicleproof.insert_one(image)  
        #    full body
        fb_detector = cv2.CascadeClassifier('haarcascade_fullbody.xml')
        fullbody = detector.detectMultiScale(frame, 1.3, 5, 0)
        for (ex, ey, ew, eh) in fullbody:
            roi_color = frame[ey:ey+eh, ex:ex+ew]
            cv2.rectangle(frame, (ex, ey), (ex+ew, ey+eh), (0, 255, 0), 2)
            FBFileName = "record/fullbody/fullbody_" + str(d3) + str(t3) + ".jpg"
            cv2.imwrite(FBFileName, roi_color)
            ima = Image.open(FBFileName)
            image_bytes = io.BytesIO()
            ima.save(image_bytes, format='JPEG')
            image = {
                'data': image_bytes.getvalue(),
                'Suspecious Activity': act,
                'date': d3,
                'time': t3
            }
            fullbodyproof.insert_one(image)

    cv2.imshow("Live Streaming", frame)
    key = cv2.waitKey(1)
    if key == ord('q') or key == ord('Q'):
        cap.release()
        cv2.destroyAllWindows()
        break