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

    vector <Rect> faces;
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
        vector <Rect> eyes;

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

void nms(const vector <Rect> &srcRects, vector <Rect> &resRects, float thresh) {
    resRects.clear();

    const size_t size = srcRects.size();
    if (!size) {
        return;
    }

    multimap<int, size_t> idxs;
    for (size_t i = 0; i < size; ++i) {
        idxs.insert(pair<int, size_t>(srcRects[i].br().y, i));
    }

    while (idxs.size() > 0) {

        auto lastElem = --idxs.end();
        const Rect &rect1 = srcRects[lastElem->second];
        resRects.push_back(rect1);
        idxs.erase(lastElem);

        for (auto pos = idxs.begin(); pos != idxs.end();) {
            const Rect &rect2 = srcRects[pos->second];

            float intArea = (rect1 & rect2).area();
            float unionArea = rect1.area() + rect2.area() - intArea;
            float overlap = intArea / unionArea;

            if (overlap > thresh) {
                idxs.erase(pos);
            } else {
                ++pos;
            }
        }
    }
}

void detectHuman(Mat &frame) {
    HOGDescriptor hog;
    hog.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());

    vector <Rect> humans;
    Mat frame_gray;

    cvtColor(frame, frame_gray, CV_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);

    hog.detectMultiScale(frame_gray, humans, 0, Size(8, 8), Size(32, 32), 1.05, 2);

    vector <Rect> resRects;
    nms(humans, resRects, 0.3f);

    for (int i = 0; i < resRects.size(); i++) {
        rectangle(frame, Point(resRects[i].x, resRects[i].y),
                  Point(resRects[i].x + resRects[i].width, resRects[i].y + resRects[i].height),
                  Scalar(0, 255, 0));
    }
}