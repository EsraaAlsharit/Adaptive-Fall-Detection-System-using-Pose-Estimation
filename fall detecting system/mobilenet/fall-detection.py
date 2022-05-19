import cv2
from tensorflow.keras.models import load_model
import pickle
import matplotlib.pyplot as plt
import numpy as np
import sys
import tensorflow as tf
sys.path.append('tf-pose-master/')
from tf_pose.estimator import TfPoseEstimator
from tf_pose.networks import get_graph_path    



def evaluate(processed, old_class, current_class):
    processed = cv2.resize(processed, (224, 224))
    input = np.array(processed).reshape((1, 224, 224, 3)).astype('float32')/255
    predictions = model.predict(input).ravel()
    print(predictions)
    prediction = np.argmax(predictions)
    current_class = classes[prediction].lower()
    print('old_class = ' + old_class + '      current_class = ' + current_class)
    if current_class == 'fall':
        cv2.putText(processed, '{0}'.format('Fall Detected'), (10, 48), cv2.FONT_HERSHEY_DUPLEX, 0.7, (0, 0, 255), 2)
    cv2.imshow('tf-pose-estimation result', processed)
    
    if current_class != old_class:
        old_class = current_class
        #if current_class == 'fall':
            #send_alert()
    return old_class, current_class

def process(frame, TfPoseEstimator):
    frame = cv2.resize(frame, (250, 250))
    humans = estimator.inference(frame, resize_to_default=True, upsample_size=4.0)
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    backtorgb = cv2.cvtColor(gray,cv2.COLOR_GRAY2RGB)
    processed = TfPoseEstimator.draw_humans(backtorgb, humans, imgcopy=False)
    return len(humans), processed
    
def load_custom_model():
    model = load_model('D:\\TEMP\\Fall Project\\workSpace\\network\\fall_detection.model')
    classes = ['fall', 'not_fall']
    model._make_predict_function()
    return model, classes

def main():
    global model, classes, estimator, old_class, current_class
    old_class = 'not_fall'
    current_class = 'not_fall'
    
    tf.compat.v1.experimental.output_all_intermediates(True)
        
    estimator = TfPoseEstimator(get_graph_path('mobilenet_thin'), target_size=(432, 368), trt_bool=False)
  
    model, classes = load_custom_model()
    
    cam = cv2.VideoCapture(0)
    ret_val, frame = cam.read()
    
    while True:
        ret_val, frame = cam.read()
        length, processed = process(frame, TfPoseEstimator)
        
        if length < 1:
            print('no humans found')
            cv2.imshow('tf-pose-estimation result', processed)
        else:
            old_class, current_class = evaluate(processed, old_class, current_class)
        
        if cv2.waitKey(1) == 27:
            break
    cv2.destroyAllWindows()

main()