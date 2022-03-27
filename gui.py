import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
from tkinter import *
import tkinter.filedialog as fd
import tkinter.messagebox as mb
import pandas as pd
import tensorflow as tf
from tensorflow import keras
import os
import cv2
import numpy as np
import os
import shutil
from datetime import datetime, date
from pdf2image import convert_from_path
import warnings
warnings.filterwarnings('ignore')
# folder
path = "record"

# Check whether the specified path exists or not
isExist = os.path.exists(path)
print(isExist)

if not isExist:
    os.makedirs(path)
    print("new directory is created!")

#Model

tflite_model_file = "obscenetmquant.tflite"
string_pred_gen = ['NonObscene', 'Obscene']
tflite_interpreter = tf.lite.Interpreter(model_path=tflite_model_file)

input_details = tflite_interpreter.get_input_details()
output_details = tflite_interpreter.get_output_details()

tflite_interpreter.resize_tensor_input(input_details[0]['index'], (1, 224, 224, 3))
tflite_interpreter.resize_tensor_input(output_details[0]['index'], (1, 2))
tflite_interpreter.allocate_tensors()

input_details = tflite_interpreter.get_input_details()
output_details = tflite_interpreter.get_output_details()

# Read file
def img_read(img_path):
    try:
        img = cv2.imread(img_path) 
        img = cv2.resize(img, (224,224))
    except:
        img = cv2.imread("xx.png")
        img = cv2.resize(img, (224,224))   
      
    return img

# Creating the backend functions for python file explorer project
def open_file():
   file = fd.askopenfilename(title='Choose a file of any type', filetypes=[("All files", "*.*")])

   os.startfile(os.path.abspath(file))


def copy_file():
   file_to_copy = fd.askopenfilename(title='Choose a file to copy', filetypes=[("All files", "*.*")])
   dir_to_copy_to = fd.askdirectory(title="In which folder to copy to?")

   try:
       shutil.copy(file_to_copy, dir_to_copy_to)
       mb.showinfo(title='File copied!', message='Your desired file has been copied to your desired location')
   except:
       mb.showerror(title='Error!', message='We were unable to copy your file to the desired location. Please try again')


def delete_file():
   file = fd.askopenfilename(title='Choose a file to delete', filetypes=[("All files", "*.*")])

   os.remove(os.path.abspath(file))

   mb.showinfo(title='File deleted', message='Your desired file has been deleted')


def open_folder():
   folder = fd.askdirectory(title="Select Folder to open")
   os.startfile(folder)


def delete_folder():
   folder_to_delete = fd.askdirectory(title='Choose a folder to delete')
   os.rmdir(folder_to_delete)
   mb.showinfo("Folder Deleted", "Your desired folder has been deleted")


def move_folder():
   folder_to_move = fd.askdirectory(title='Select the folder you want to move')
   mb.showinfo(message='You just selected the folder to move, now please select the desired destination where you want to move the folder to')
   destination = fd.askdirectory(title='Where to move the folder to')

   try:
       shutil.move(folder_to_move, destination)
       mb.showinfo("Folder moved", 'Your desired folder has been moved to the location you wanted')
   except:
       mb.showerror('Error', 'We could not move your folder. Please make sure that the destination exists')


def list_files_in_folder():
   i = 0

   folder = fd.askdirectory(title='Select the folder whose files you want to list')

   files = os.listdir(os.path.abspath(folder))
   dirs= os.listdir(os.path.abspath(folder))
   data_patht=[]
   for j in dirs:
    #if j.endswith(".png"):
    abs_path=os.path.abspath(folder)+"/"+j
    data_patht.append(abs_path)
   list_files_wn = Toplevel(root)
   list_files_wn.title(f'Files in {folder}')
   list_files_wn.geometry('1000x1000')
   list_files_wn.resizable(0, 0)

   listbox = Listbox(list_files_wn, selectbackground='SteelBlue', font=("Georgia", 10))
   listbox.place(relx=0, rely=0, relheight=1, relwidth=1)

   scrollbar = Scrollbar(listbox, orient=VERTICAL, command=listbox.yview)
   scrollbar.pack(side=RIGHT, fill=Y)

   listbox.config(yscrollcommand=scrollbar.set)
   test_label=[]
   test_name=[]
   #while i < len(files):
    #   listbox.insert(END, files[i])
     #  i += 1
       
   for i in range(0,len(data_patht)):
   # pages = convert_from_path(data_patht[i], 500)
    img=img_read(data_patht[i]).reshape(-1,224,224,3)
    #img=img_read(pages).reshape(-1,224,224,3)
    img = np.uint8(img)
    tflite_interpreter.set_tensor(input_details[0]['index'], img)
    tflite_interpreter.invoke()
    tflite_model_predictions = tflite_interpreter.get_tensor(output_details[0]['index'])
    index_pred_obs = int(np.argmax(tflite_model_predictions))
    prezic_obs = string_pred_gen[index_pred_obs]
    test_name.append(prezic_obs)
    test_label.append(tflite_model_predictions/256)  
    listbox.insert(END, data_patht[i],test_label[i],test_name[i])
   nsfw=0
   sfw=0

   for i in range(0, len(test_label)):
    if(test_label[i][0][0]>test_label[i][0][1]):
        sfw = sfw+1
    else:
        nsfw = nsfw+1

    print("Obscene frames", nsfw)
    print("Noobs Frames", sfw)
   Time = []
   Date = []
   Name = []
   result=[]
   Score=[]
   Pred=[]
   for i in range(0, len(files)):
    # Timing
    now = datetime.now()
    today = date.today()
    d3 = today.strftime("%B%d%Y")
    t3 = now.strftime("%Hh%Mm%Ss")
    Time.append(t3)
    Date.append(d3)
    Name.append(files[i])
    Score.append(test_label[i])
    Pred.append(test_name[i])

    dict = {'File Name':Name, 'Testing Time': Time, 'Testing Date': Date, 'Confidence Scores':Score, "Predicted Class":Pred}
    df = pd.DataFrame(dict)
    df.to_csv('record/record.csv')
    df.to_excel('record/recording.xlsx')
    result.append(str(files[i])+","+str(test_label[i])+","+str(test_name[i]))
        
   with open('record/Final_Prediction_obscene.txt', 'w') as f:
    for item in result:
        f.write("%s\n" % item)
    

# Defining the variables
title = 'CAWACH GUI'
background = '#FFF000'

button_font = ("Times New Roman", 13)
button_background = '#FFFFFF'#'Turquoise'

# Initializing the window
root = Tk()
root.title(title)
root.geometry('550x1000')
root.resizable(0, 0)
#root.config(bg=background)
root.attributes("-fullscreen", False)
root.protocol("WM_DELETE_WINDOW", root.iconify)
root.bind('<Escape>', lambda e: root.quit()) # Optional quit
menubar = Menu(root)
filemenu = Menu(menubar, tearoff=0)
menubar.add_cascade(label="Exit", command=root.destroy)
root.configure(background='#808080',highlightthickness=5, highlightcolor='black',menu=menubar)

# Creating and placing the components in the window
Label(root, text=title, font=("Times New Roman", 15), bg=background, wraplength=250).place(x=20, y=0)
Button(root, text='Open a file', width=20, font=button_font, bg=button_background, command=open_file).place(x=30, y=50)

Button(root, text='Copy a file', width=20, font=button_font, bg=button_background, command=copy_file).place(x=30, y=90)

Button(root, text='Delete a file', width=20, font=button_font, bg=button_background, command=delete_file).place(x=30, y=130)

Button(root, text='Open a folder', width=20, font=button_font, bg=button_background, command=open_folder).place(x=30, y=170)

Button(root, text='Delete a folder', width=20, font=button_font, bg=button_background, command=delete_folder).place(x=30, y=210)

Button(root, text='Move a folder', width=20, font=button_font, bg=button_background, command=move_folder).place(x=30, y=250)

Button(root, text='Check the files', width=20, font=button_font, bg=button_background, command=list_files_in_folder).place(x=30, y=290)

# Finalizing the window
root.update()
root.mainloop()