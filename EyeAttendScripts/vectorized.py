# -*- coding: utf-8 -*-
"""
Created on Tue Nov  3 09:21:26 2020

@author: tangr
"""
#Import Packages
import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '0'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '1'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '2'
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'  # or any {'0', '1', '2'}
import json
from tensorflow.keras.preprocessing.image import img_to_array,array_to_img
from tensorflow.keras.models import load_model
from sqlalchemy import create_engine
from keras_facenet import FaceNet
#from mtcnn.mtcnn import MTCNN
from datetime import date
import mysql.connector
from PIL import Image
import pandas as pd
import numpy as np
import argparse
import warnings
import pickle

#import cv2

warnings.filterwarnings("ignore")
#Global List for storing presenties roll number
attendence = {} 
absentees = {}

#Argument Parser
ap = argparse.ArgumentParser()
#Image arg parser
ap.add_argument("-i","--image",required = True,
				help = 'path to input images')

ap.add_argument("-br","--branch",required = True,
                help = 'students branch')

ap.add_argument("-ba","--batch",required = True,
                help = 'students batch')

#Self trained Mask Detector model
ap.add_argument("-m","--model",type = str,
				default = 'mask_224_detector.model',
				help='path to trained mask detector model')

#Self trained Real Fake Model
ap.add_argument('-rf','--rf_model',type = str,
				default = 'model_wo_mask/real_fake_wo_mask.model',
				help = 'path to trained real fake model')

#Real Fake model labels
ap.add_argument('-rfl','--rf_labels',type = str,
				default = 'model_wo_mask/real_fake_wo_mask_labels.pickle',
				help = 'path to labels for real fake')

#Self trained Real Fake Model With Mask
ap.add_argument('-rfm','--rfm_model',type = str,
				default = 'model_w_mask/real_fake_w_mask.model',
				help = 'path to trained real fake model with mask')

#Real Fake model labels with mask
ap.add_argument('-rflm','--rfm_labels',type = str,
				default = 'model_w_mask/real_fake_w_mask_labels.pickle',
				help = 'path to labels for real fake model with mask')

ap.add_argument("-c", "--confidence", type=float, default=0.5,
	help="minimum probability to filter weak detections")

args = vars(ap.parse_args())


#classroom path
class_path = "classroom"
#Final Image path
face_path = "images"


def checkTableExists(dbcur, tablename):
    dbcur.execute("SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '{0}'".format(tablename.replace('\'', '\'\'')))
    if dbcur.fetchone()[0] == 1:
        return True
    return False

def checkColumnExists(dbcur,tablename,columnname):
    dbcur.execute("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='eyeattend' AND TABLE_NAME='{0}' AND column_name='{1}'".format(tablename,columnname))
    if dbcur.fetchone()[0] == 1:
        return True
    return False


def fetchresult(branch,batch,prof_table):
    table_name = 'batch_'+str(batch)
    conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="",
    database="eyeattend"
    )
    if conn.is_connected():
        print("Successfully connected")
        df = pd.read_sql(f"SELECT roll_no,photo_embedd FROM {table_name} WHERE branch=(%s) AND batch = (%s)",con=conn,params=(branch,batch))
        #results = mycursor.fetchall()
        #df = pd.DataFrame(results)
        conn.commit()
        print('success in fetching')
        # Closing the connection

        db_cur = conn.cursor()
        if not checkTableExists(db_cur,prof_table):
            query = f"CREATE TABLE IF NOT EXISTS {prof_table}(roll_no VARCHAR(9) PRIMARY KEY)"
            db_cur.execute(query)
            conn.commit()
            engine = create_engine("mysql+mysqlconnector://{user}:{pw}@{host}/{db}"
                .format(host="localhost", db="eyeattend", user="root", pw=""))
            df['roll_no'].to_sql(f"{prof_table}", con = engine, if_exists = 'append', chunksize = 1000,index=False)
            
        conn.commit()
        conn.close()
        return df

    else:
        print('Error in connecting with database')
        return -1
    


def calculate_attendance(image):
    #######here using the model we will convert the image to embeddings
    # Gets a detection dict for each face
    # in an image. Each one has the bounding box and
    # face landmarks (from mtcnn.MTCNN) along with
    # the embedding from FaceNet.
    imgarray = img_to_array(image)
    detections = embedder.extract(imgarray, threshold=0.95)

    
    length = len(detections)
    for i in range(length):
        x1, y1, width, height = detections[i]['box']
        #calculating Box Dims
        x1, y1 = abs(x1), abs(y1)
        x2, y2 = x1 + width, y1 + height

        #Taking face from Complete image
        face_im = imgarray[y1:y2, x1:x2]
        face = array_to_img(face_im)
        face = img_to_array(face.resize((224,224)))
        face = np.expand_dims(face, axis=0)
		#detecting for mask
        (mask,withoutMask) = mask_detector.predict(face)[0]
		
        if mask > withoutMask:
            print("Mask")
            #Mask conditions 

        else:
            print("No Mask")
            #No mask Conditions
			#Will check for real and fake now
            label = rf_le.classes_[np.argmax(real_fake_detector.predict(face)[0])]
            #Checking label
            if label == "real":
				#We will do stuff here and ignore for fake faces
				#NO PROXY ALLOWED !!!
                embedding = embedder.embeddings(face)
                
                difference = picklefiles-embedding
                normedvector = np.sum(np.abs(difference)**2,axis=-1)**(1./2)
                index=np.argmin(normedvector)
                if normedvector[index] < 0.9:
                    attendence[str(i)] = results['roll_no'][index]
                    print(results['roll_no'][index],normedvector[index])
                else:
                    print("Not in the Database")
    else:
        conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="",
        database="eyeattend"
        )
        if conn.is_connected():

            db_cur = conn.cursor()

                       
            if not checkColumnExists(db_cur,prof_table,str(date.today())):
                query = "ALTER TABLE `{0}` ADD `{1}` varchar(1) NOT NULL DEFAULT 'A'".format(prof_table, str(date.today()))
                db_cur.execute(query)
                conn.commit()
                
            results[str(date.today())] = ['P' if results['roll_no'][index] in attendence.values() else 'A' for index in range(len(results))]

            for i in range(len(results)):
                r = results['roll_no'][i]
                if results[str(date.today())][i] == "A":
                    absentees[i] = r
                query_up = f"UPDATE `{prof_table}` SET `{str(date.today())}`= '{results[str(date.today())][i]}' WHERE `roll_no` = '{r}'"
                db_cur.execute(query_up)
                conn.commit()  


            total_att = {}
            total_att['present'] = json.dumps(attendence)
            total_att['absent'] = json.dumps(absentees)
            print(json.dumps(total_att))
            conn.close()
        else:
            print('Error in connecting with database')
            return -1

                    
prof_table = args['image'].split("/")[-1]
prof_table = prof_table.split(".")[0]

stu_branch = str(args['branch'])
stu_batch = str(args['batch'])

results = fetchresult(stu_branch,stu_batch,prof_table)
if isinstance(results, int):
    if results == -1:
        exit(0)
picklefiles = [np.array(pickle.loads(results['photo_embedd'][i])) for i in range(len(results))]
picklefiles=np.squeeze(picklefiles)


#Loading all Models
print("Loading All Models...")
real_fake_detector = load_model("/opt/lampp/htdocs/EyeAttendScripts/model_wo_mask/real_fake_wo_mask.model")
print("Real fake without mask loaded")

mask_detector = load_model("/opt/lampp/htdocs/EyeAttendScripts/mask_224_detector.model")
print("Mask Detector loaded !")
embedder = FaceNet()
#face_detector = MTCNN()
print("Facenet_MTCNN Loaded")
#model_face = load_model('facenet_keras.h5')
print("Facenet Loaded")


#Reading real fake labels
rf_le = pickle.loads(open('/opt/lampp/htdocs/EyeAttendScripts/model_wo_mask/real_fake_wo_mask_labels.pickle', "rb").read())
#rf_le_w_mask = pickle.loads(open(args["rfm_labels"], "rb").read())
print("All Models Loaded")



#reading Image
image = Image.open(args['image'])
#Detecting Faces
calculate_attendance(image)

with open('attendence.txt', 'w') as f:
    for item in attendence.values():
        f.write("%s\n" % item)
#image = array_to_img(pixels)
#image.save(face_path + "/final.jpg")
