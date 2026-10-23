import tensorflow as tf
interpreter = tf.lite.Interpreter(model_path="facenet.tflite")
interpreter.allocate_tensors()
print("INPUT:", interpreter.get_input_details()[0]['shape'])
print("OUTPUT:", interpreter.get_output_details()[0]['shape'])
