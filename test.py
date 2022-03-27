import tensorflow as tf
from tensorflow import keras
import os
import cv2
import numpy as np

os.environ["CUDA_VISIBLE_DEVICES"] = "1"

tflite_model_file = "/DATA/saurabh_2021cs30/project/NSFW_Pro/obscenetNVquant.tflite"

tflite_interpreter = tf.lite.Interpreter(model_path=tflite_model_file)

input_details = tflite_interpreter.get_input_details()
output_details = tflite_interpreter.get_output_details()

tflite_interpreter.resize_tensor_input(input_details[0]['index'], (1, 224, 224, 3))
tflite_interpreter.resize_tensor_input(output_details[0]['index'], (1, 2))
tflite_interpreter.allocate_tensors()

input_details = tflite_interpreter.get_input_details()
output_details = tflite_interpreter.get_output_details()

print("== Input details ==")
print("shape:", input_details[0]['shape'])
print("\n== Output details ==")
print("shape:", output_details[0]['shape'])


def img_read(img_path):
   try:
      img = cv2.imread(img_path) 
      img = cv2.resize(img, (224,224))
   except:
      img = cv2.imread("/home2/shikhar_1911mc13/Intern/copy_Training_keyframes/NONFIGHT/fight_0001_7.jpeg")
      img = cv2.resize(img, (224,224))   
      
   return img
   
path="/DATA/saurabh_2021cs30/project/test/nsfw"
data_patht=[]

dirs=os.listdir(path)
for j in dirs:
  abs_path=path+"/"+j
  data_patht.append(abs_path)


test_label=[]
for i in range(0,len(data_patht)):
   img=img_read(data_patht[i]).reshape(-1,224,224,3)
   img = np.uint8(img)
   tflite_interpreter.set_tensor(input_details[0]['index'], img)
   tflite_interpreter.invoke()
   tflite_model_predictions = tflite_interpreter.get_tensor(output_details[0]['index'])
   test_label.append(tflite_model_predictions)
   
nsfw=0
sfw=0

for i in range(0, len(test_label)):
   if(test_label[i][0][0]>test_label[i][0][1]):
      sfw = sfw+1
   else:
      nsfw = nsfw+1

print("Obscene frames", nsfw)
print("Noobs Frames", sfw)
   
result=[]
for i in range(0, len(data_patht)):
   result.append(str(data_patht[i])+","+str(test_label[i]))
      
with open('Final_Prediction_obsceneNV.txt', 'w') as f:
 for item in result:
     f.write("%s\n" % item)


path="/DATA/saurabh_2021cs30/project/test/sfw"
data_patht=[]

dirs=os.listdir(path)
for j in dirs:
  abs_path=path+"/"+j
  data_patht.append(abs_path)


test_label=[]
for i in range(0,len(data_patht)):
   img=img_read(data_patht[i]).reshape(-1,224,224,3)
   img = np.uint8(img)
   tflite_interpreter.set_tensor(input_details[0]['index'], img)
   tflite_interpreter.invoke()
   tflite_model_predictions = tflite_interpreter.get_tensor(output_details[0]['index'])
   test_label.append(tflite_model_predictions)
   
nsfw=0
sfw=0

for i in range(0, len(test_label)):
   if(test_label[i][0][0]>test_label[i][0][1]):
      sfw = sfw+1
   else:
      nsfw = nsfw+1

print("Fight frames", nsfw)
print("No Fight Frames", sfw)
   
result=[]
for i in range(0, len(data_patht)):
   result.append(str(data_patht[i])+","+str(test_label[i]))
      
with open('Final_Prediction_NonobsceneNV.txt', 'w') as f:
 for item in result:
     f.write("%s\n" % item)
