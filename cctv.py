"""
Run classification on Camera, Press ESC to exit the program
For Raspberry PI, please use `import tflite_runtime.interpreter as tflite` instead
"""
from pygame.locals import *
import pygame.camera
import pygame
import socket
from datetime import datetime, date
from multiprocessing import Process
import threading
import warnings

from openpyxl import Workbook
import pyttsx3
import io
from PIL import Image
from pymongo import MongoClient
from pygame import mixer
import pandas as pd
import sys
import time
import numpy as np
import tensorflow.lite as tflite
from tensorflow.keras.preprocessing.image import img_to_array
import tensorflow as tf  # version 1.14
import cv2
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
warnings.filterwarnings('ignore')

# from threading import *


# import tflite_runtime.interpreter as tflite


CAMERA_WIDTH = 640
CAMERA_HEIGHT = 480


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


def load_labels(label_path):
    r"""Returns a list of labels"""
    with open(label_path, 'r') as f:
        return [line.strip() for line in f.readlines()]


def load_model(model_path):
    r"""Load TFLite model, returns a Interpreter instance."""
    interpreter = tflite.Interpreter(model_path=model_path)
    interpreter.allocate_tensors()
    return interpreter


def process_image(interpreter, image, input_index, k=3):
    r"""Process an image, Return top K result in a list of 2-Tuple(confidence_score, _id)"""
    input_data = np.expand_dims(image, axis=0)  # expand to 4-dim

    # Process
    interpreter.set_tensor(input_index, input_data)
    interpreter.invoke()

    # Get outputs
    output_details = interpreter.get_output_details()
    output_data = interpreter.get_tensor(output_details[0]['index'])
    # print(output_data.shape)  # (1, 1001)
    output_data = np.squeeze(output_data)

    # Get top K result
    top_k = output_data.argsort()[-k:][::-1]  # Top_k index
    result = []
    for _id in top_k:
        score = float(output_data[_id] / 255.0)
        result.append((_id, score))

    return result


def display_result(top_result, frame, labels):
    r"""Display top K result in top right corner"""
    font = cv2.FONT_HERSHEY_SIMPLEX
    size = 0.6
    color = (255, 0, 0)  # Blue color
    thickness = 1

    for idx, (_id, score) in enumerate(top_result):
        # print('{} - {:0.4f}'.format(label, score))
        x = 12
        y = 24 * idx + 24
        cv2.putText(frame, '{} - {:0.4f}'.format(labels[_id], score),
                    (x, y), font, size, color, thickness)

    cv2.imshow('Image Classification', frame)


if __name__ == "__main__":

    model_path = './data/scuffle_tm_quant.tflite'
    model_path1 = './data/obscenetmquant.tflite'
    label_path = './data/labels_scuffle.txt'
    label_path1 = './data/labels_obscene.txt'

    cap = cv2.VideoCapture('trfign0.mp4')
    cap.set(cv2.CAP_PROP_FRAME_WIDTH, CAMERA_WIDTH)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, CAMERA_HEIGHT)
    cap.set(cv2.CAP_PROP_FPS, 15)

    interpreter = load_model(model_path)
    interpreter1 = load_model(model_path1)
    labels = load_labels(label_path)
    labels1 = load_labels(label_path1)

    input_details = interpreter.get_input_details()
    input_details1 = interpreter1.get_input_details()

    # Get Width and Height
    input_shape = input_details[0]['shape']
    input_shape1 = input_details1[0]['shape']
    height = input_shape[1]
    height = input_shape1[1]
    width = input_shape[2]
    width = input_shape1[2]

    # Get input index
    input_index = input_details[0]['index']
    input_index1 = input_details1[0]['index']

    # Process Stream
    while True:
        ret, frame = cap.read()

        image = Image.fromarray(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
        image = image.resize((width, height))

        top_result = process_image(interpreter, image, input_index)
        top_result1 = process_image(interpreter1, image, input_index1)

        # folder
        path = "./record"

        # Check whether the specified path exists or not
        isExist = os.path.exists(path)
        print(isExist)

        if not isExist:
            os.makedirs(path)
            print("new directory is created!")

        if isExist:
            path_w = "./record/weapon"
            isExist = os.path.exists(path_w)
            print(isExist)

        if not isExist:
            os.makedirs(path_w)
            print("new directory is created!")

        path_v = "./record/vehicle"
        isExist = os.path.exists(path_v)
        print(isExist)

        if not isExist:
            os.makedirs(path_v)
            print("new directory is created!")

        path_f = "./record/face"
        isExist = os.path.exists(path_f)
        print(isExist)

        if not isExist:
            os.makedirs(path_f)
            print("new directory is created!")

        path_fb = "./record/fullbody"
        isExist = os.path.exists(path_fb)
        print(isExist)

        if not isExist:
            os.makedirs(path_fb)
            print("new directory is created!")

        path_scf = "./record/scfproof"
        isExist = os.path.exists(path_scf)
        print(isExist)

        if not isExist:
            os.makedirs(path_scf)
            print("new directory is created!")

        path_obs = "./record/obsproof"
        isExist = os.path.exists(path_obs)
        print(isExist)

        if not isExist:
            os.makedirs(path_obs)
            print("new directory is created!")

        import csv

        with open('./record/record.csv', 'wb') as csvfile:
            filewriter = csv.writer(
                csvfile, delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)

        wb = Workbook("./record/record.xlsx")
        wb.save("./record/record.xlsx")

        # Timing
        now = datetime.now()
        start_time = 10
        t3 = now.strftime("%Hh%Mm%Ss")
        today = date.today()
        d3 = today.strftime("%B%d%Y")

        '''
        # Audio Alert
        def alert():
            mixer.init()
            mixer.music.load("beep.mp3")
            mixer.music.play()
            time.sleep(1)
            mixer.music.stop()
        '''
        client = MongoClient(
            "mongodb://localhost:27017")
        db = client.edgedev
        obsproof = db.obsproof
        faceproof = db.faceproof
        vehicleproof = db.vehicleproof
        weaponproof = db.weaponproof
        fullbodyproof = db.fullbodyproof
        scfproof = db.scfproof
        wproof = db.wproof

        # Prediction mechanism obscene
        string_pred_gen = ['Obscene', 'NonObscene']
        interpreter_obs = load_model(model_path="obscenetmquant.tflite")
        interpreter_obs = tflite.Interpreter(
            model_path="obscenetmquant.tflite")
        interpreter_obs.allocate_tensors()
        input_details_obs = interpreter_obs.get_input_details()
        output_details_obs = interpreter_obs.get_output_details()
        input_shape_obs = input_details_obs[0]['shape']

        # Prediction mechanism scuffle
        string_pred_gena = ['NoScuffle', 'Scuffle']
        interpreter_scuf = load_model(model_path="scuffle_tm_quant.tflite")
        interpreter_scuf = tflite.Interpreter(
            model_path="scuffle_tm_quant.tflite")
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
        cap = cv2.VideoCapture('trfign0.mp4')
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

            input_im = cv2.resize(input_im, (224, 224))
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
             # Get the information of the incoming image type
            # data = data.astype(np.float64) / info.max # normalize the data to 0 - 1
            # data = 255 * data # Now scale by 255
            
            input_data = np.array(input_im)
            input_data=input_data.astype(np.uint8)
            interpreter_obs.set_tensor(
                input_details_obs[0]['index'], input_data)
            interpreter_obs.invoke()
            interpreter_scuf.set_tensor(
                input_details_scuf[0]['index'], input_data)
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
                # act = "Obscene and Scuffle"
                # OSFileName = "record/obsProof/obs_" + \
                #     str(d3) + str(t3) + ".jpg"
                # cv2.imwrite(OSFileName, frame)
                # OSFileName = "record/scfProof/scf_" + \
                #     str(d3) + str(t3) + ".jpg"
                # cv2.imwrite(OSFileName, frame)
                # ima = Image.open(OSFileName)
                # image_bytes = io.BytesIO()
                # ima.save(image_bytes, format='JPEG')
                # image = {
                #     'data': image_bytes.getvalue(),
                #     'Suspecious Activity': act,
                #     'date': d3,
                #     'time': t3
                # }
                # obsproof.insert_one(image)
                # scfproof.insert_one(image)

            elif (prezic_obs == 'Obscene') and (prezic_scuf != 'Scuffle'):
                cv2.putText(frame, prezic_obs, (40, 50), font,
                            2, (0, 0, 255), 5, cv2.LINE_AA)
                cv2.putText(frame, prezic_scuf, (40, 100), font,
                            2, (255, 255, 255), 1, cv2.LINE_AA)
                alert()
                # act = "Obscene"
                # OFileName = "record/obsProof/obs_" + str(d3) + str(t3) + ".jpg"
                # cv2.imwrite(OFileName, frame)
                # ima = Image.open(OFileName)
                # image_bytes = io.BytesIO()
                # ima.save(image_bytes, format='JPEG')
                # image = {
                #     'data': image_bytes.getvalue(),
                #     'Suspecious Activity': act,
                #     'date': d3,
                #     'time': t3
                # }
                # obsproof.insert_one(image)

            elif (prezic_obs != 'Obscene') and (prezic_scuf == 'Scuffle'):
                cv2.putText(frame, prezic_obs, (40, 50), font,
                            2, (255, 255, 255), 1, cv2.LINE_AA)
                cv2.putText(frame, prezic_scuf, (40, 100), font,
                            2, (0, 0, 255), 5, cv2.LINE_AA)
                alert()
                # act = "Scuffle"
                # SFileName = "record/scfProof/scf_" + str(d3) + str(t3) + ".jpg"
                # cv2.imwrite(SFileName, frame)
                # ima = Image.open(SFileName)
                # image_bytes = io.BytesIO()
                # ima.save(image_bytes, format='JPEG')
                # image = {
                #     'data': image_bytes.getvalue(),
                #     'Suspecious Activity': act,
                #     'date': d3,
                #     'time': t3
                # }
                # scfproof.insert_one(image)

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

            # or (indexes == 0):
            if(prezic_obs == 'Obscene') or (prezic_scuf == 'Scuffle'):
                Time.append(t3)
                Date.append(d3)
                Act.append(act)
                dict = {'Time': Time, 'Date': Date, 'Activity': Act}
                df = pd.DataFrame(dict)
                df.to_csv('record/record.csv')
                df.to_excel('record/recording.xlsx')
                detector = cv2.CascadeClassifier(
                    'haarcascade_frontalface_default.xml')
                faces = detector.detectMultiScale(frame, 1.3, 5, 0)
                for (ex, ey, ew, eh) in faces:
                    roi_color = frame[ey:ey+eh, ex:ex+ew]
                    cv2.rectangle(frame, (ex, ey),
                                  (ex+ew, ey+eh), (0, 255, 0), 2)
                    FFileName = "record/face/faces_" + \
                        str(d3) + str(t3) + ".jpg"
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
                w_detector = cv2.CascadeClassifier('gun_cascade.xml')
                weapons = detector.detectMultiScale(frame, 1.3, 5, 0)
                for (ex, ey, ew, eh) in weapons:
                    roi_color = frame[ey:ey+eh, ex:ex+ew]
                    cv2.rectangle(frame, (ex, ey),
                                  (ex+ew, ey+eh), (0, 255, 0), 2)
                    WFileName = "./record/weapon/weapons_" + \
                        str(d3) + str(t3) + ".jpg"
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

                # vehicle
                v_detector = cv2.CascadeClassifier('cascade.xml')
                vehicles = detector.detectMultiScale(frame, 1.3, 5, 0)
                for (ex, ey, ew, eh) in vehicles:
                    roi_color = frame[ey:ey+eh, ex:ex+ew]
                    cv2.rectangle(frame, (ex, ey),
                                  (ex+ew, ey+eh), (0, 255, 0), 2)
                    VFileName = "./record/vehicle/vehicles_" + \
                        str(d3) + str(t3) + ".jpg"
                    cv2.imwrite(VFileName, roi_color)
                    ima = Image.open(VFileName)
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
                    cv2.rectangle(frame, (ex, ey),
                                  (ex+ew, ey+eh), (0, 255, 0), 2)
                    FBFileName = "./record/fullbody/fullbody_" + \
                        str(d3) + str(t3) + ".jpg"
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

                # face
                face_detector = cv2.CascadeClassifier(
                    'haarcascade_frontalface_default.xml')
                face = detector.detectMultiScale(frame, 1.3, 5, 0)
                for (ex, ey, ew, eh) in face:
                    roi_color = frame[ey:ey+eh, ex:ex+ew]
                    cv2.rectangle(frame, (ex, ey),
                                  (ex+ew, ey+eh), (0, 255, 0), 2)
                    FCFileName = "./record/face/face_" + \
                        str(d3) + str(t3) + ".jpg"
                    cv2.imwrite(FCFileName, roi_color)
                    ima = Image.open(FCFileName)
                    image_bytes = io.BytesIO()
                    ima.save(image_bytes, format='JPEG')
                    image = {
                        'data': image_bytes.getvalue(),
                        'Suspecious Activity': act,
                        'date': d3,
                        'time': t3
                    }
                    faceproof.insert_one(image)

            cv2.imshow("Live Streaming", frame)
            key = cv2.waitKey(1)
            if key == ord('q') or key == ord('Q'):
                cap.release()
                cv2.destroyAllWindows()
                break

        display_result(top_result, frame, labels)
        display_result(top_result1, frame, labels)

        key = cv2.waitKey(1)
        if key == 27:  # esc
            break

        cap.release()
        cv2.destroyAllWindows()
