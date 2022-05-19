import socket
import json
import cv2
import base64
import numpy as np

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((socket.gethostname(), 1243))

while True:
    full_msg = ''
    new_msg = True
    while True:
        msg = s.recv(10240000).decode('utf-8')
        parsed_data = json.loads(msg)
        print('current_class = ' + parsed_data["current_class"])
        print('old_class = ' + parsed_data["old_class"])
        string = parsed_data['image']
        jpg_original = base64.b64decode(string)
        jpg_as_np = np.frombuffer(jpg_original, dtype=np.uint8)
        img = cv2.imdecode(jpg_as_np, flags=1)
        cv2.imwrite('client.jpg', img)