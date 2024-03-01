import cv2
import pytesseract
import os
import sys
import time
import numpy as np
from PIL import ImageGrab
from PIL import Image

# 設定 YOLOv4 相關的參數
CONF_THRESHOLD = 0.5
NMS_THRESHOLD = 0.4
YOLO_CONFIG_PATH = "D:\\Yolo_v4\\darknet\\build\darknet\\x64\\cfg\\yolov4.cfg"
YOLO_WEIGHTS_PATH = "D:\\Yolo_v4\\darknet\\build\darknet\\x64\\yolov4.weights"
YOLO_LABELS_PATH = "D:\\Yolo_v4\\darknet\\build\\darknet\\x64\\data\\coco.names"
YOLO_NET = cv2.dnn.readNetFromDarknet(YOLO_CONFIG_PATH, YOLO_WEIGHTS_PATH)
YOLO_LABELS = open(YOLO_LABELS_PATH).read().strip().split("\n")

# 設定 pytesseract 相關的參數
TESSERACT_CONFIG = "--psm 6"


def main():

    folder_path = "D:/xampp/htdocs/Upload_Image_Android/images"
    for filename in os.listdir(folder_path):
        fpath = os.path.join(folder_path, filename)

    file_path=fpath.replace('\\','/')
    
    img = cv2.imread(file_path)

    # 將圖片轉換成 YOLOv4 所需的格式
    blob = cv2.dnn.blobFromImage(img, 1/255, (416, 416), swapRB=True, crop=False)

    # 將圖片輸入到 YOLOv4 模型中進行預測
    YOLO_NET.setInput(blob)
    layer_names = YOLO_NET.getLayerNames()
    output_layer_names = [layer_names[i[0] - 1] for i in YOLO_NET.getUnconnectedOutLayers()]
    outputs = YOLO_NET.forward(output_layer_names)

    # 解析模型的輸出，獲取檢測到的物件
    boxes = []
    confidences = []
    class_ids = []

    for output in outputs:
        for detection in output:
            scores = detection[5:]
            class_id = np.argmax(scores)
            confidence = scores[class_id]
            if confidence > CONF_THRESHOLD:
                center_x = int(detection[0] * img.shape[1])
                center_y = int(detection[1] * img.shape[0])
                width = int(detection[2] * img.shape[1])
                height = int(detection[3] * img.shape[0])
                x = int(center_x - width / 2)
                y = int(center_y - height / 2)
                boxes.append([x, y, width, height])
                confidences.append(float(confidence))
                class_ids.append(class_id)

    # 使用非最大抑制方法去除重疊的邊界框
    indices = cv2.dnn.NMSBoxes(boxes, confidences, CONF_THRESHOLD, NMS_THRESHOLD)

    # 逐一處理每個檢測到的物件
    #detected_text = []
    name_text=[]
    
    for i in indices:
        i = i[0]
        box = boxes[i]


        timestamp=time.time()
        timestamp=str(timestamp)
        timestamp=timestamp.replace(".","")

        text = "{}: {:.4f}".format(YOLO_LABELS[class_ids[i]], confidences[i])
        textPath=text.replace(': ','_')
        savePath="D:/xampp/htdocs/ListView_Android/classItems/"+textPath+".jpg"
        if(box[0]>0 and box[2]>55 and box[3]>55):
            timestamp=time.time()
            timestamp=str(timestamp)
            timestamp=timestamp.replace(".","")
            crop_img=img[box[1]:box[1]+box[3],box[0]:box[0]+box[2]]
            savePath="D:/xampp/htdocs/ListView_Android/classItems/"+timestamp+"-"+textPath+".jpg"
            cv2.imwrite(savePath,crop_img)
            
    print("success",end="")

main()