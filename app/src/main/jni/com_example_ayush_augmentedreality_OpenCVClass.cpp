#include "com_example_ayush_augmentedreality_OpenCVClass.h"

JNIEXPORT void JNICALL Java_com_example_ayush_augmentedreality_OpenCVClass_faceDetection
        (JNIEnv *, jclass, jlong addrRgba) {

    Mat &frame = *(Mat *) addrRgba;
    detectFace(frame);
}

JNIEXPORT void JNICALL Java_com_example_ayush_augmentedreality_OpenCVClass_humanDetection
        (JNIEnv *, jclass, jlong addrRgba) {

    Mat &frame = *(Mat *) addrRgba;
    detectHuman(frame);

}

void detectFace(Mat &frame) {
    String face_cascade_name = "storage/emulated/0/data/haarcascade_frontalface_alt.xml";
    String eyes_cascade_name = "storage/emulated/0/data/haarcascade_eye_tree_eyeglasses.xml";
    CascadeClassifier face_cascade;
    CascadeClassifier eyes_cascade;

    if (!face_cascade.load(face_cascade_name)) {
        printf("--(!)Error loading\n");
        return;
    };
    if (!eyes_cascade.load(eyes_cascade_name)) {
        printf("--(!)Error loading\n");
        return;
    };

    std::vector <Rect> faces;
    Mat frame_gray;

    cvtColor(frame, frame_gray, CV_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);

    //-- Detect faces
    face_cascade.detectMultiScale(frame_gray, faces, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));

    for (size_t i = 0; i < faces.size(); i++) {
        Point center(faces[i].x + faces[i].width * 0.5, faces[i].y + faces[i].height * 0.5);
        ellipse(frame, center, Size(faces[i].width * 0.5, faces[i].height * 0.5), 0, 0, 360,
                Scalar(255, 0, 255), 4, 8, 0);

        Mat faceROI = frame_gray(faces[i]);
        std::vector <Rect> eyes;

        //-- In each face, detect eyes
        eyes_cascade.detectMultiScale(faceROI, eyes, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));

        for (size_t j = 0; j < eyes.size(); j++) {
            Point center(faces[i].x + eyes[j].x + eyes[j].width * 0.5,
                         faces[i].y + eyes[j].y + eyes[j].height * 0.5);
            int radius = cvRound((eyes[j].width + eyes[j].height) * 0.25);
            circle(frame, center, radius, Scalar(255, 0, 0), 4, 8, 0);
        }
    }


}

void detectHuman(Mat &frame) {
    String human_cascade_name = "storage/emulated/0/data/haarcascade_fullbody.xml";
    CascadeClassifier human_cascade;
    if (!human_cascade.load(human_cascade_name)) {
        printf("--(!)Error loading\n");
        return;
    };

    std::vector <Rect> humans;
    Mat frame_gray;

    cvtColor(frame, frame_gray, CV_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);

    //-- Detect humans
    human_cascade.detectMultiScale(frame_gray, humans, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE,
                                   Size(30, 30));
    for (int i = 0; i < humans.size(); i++) {
        rectangle(frame, Point(humans[i].x, humans[i].y),
                  Point(humans[i].x + humans[i].width, humans[i].y + humans[i].height),
                  Scalar(0, 255, 0));
    }
}

