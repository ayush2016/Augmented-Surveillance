#include "com_example_ayush_augmentedreality_OpenCVClass.h"

JNIEXPORT void JNICALL Java_com_example_ayush_augmentedreality_OpenCVClass_faceDetection
        (JNIEnv *, jclass, jlong addrRgba) {

    Mat &frame = *(Mat *) addrRgba;
    detectFace(frame);
}

JNIEXPORT void JNICALL Java_com_example_ayush_augmentedreality_OpenCVClass_humanDetection
        (JNIEnv *, jclass, jlong addrRgba, jdouble d12, jdouble d23, jdouble d31) {

    Mat &frame = *(Mat *) addrRgba;
    double distance12 = d12;
    double distance23 = d23;
    double distance31 = d31;
    detectHuman(frame, distance12, distance23, distance31);

}

double real_height = 1750.0; //mm
double focal_length = 3.0; //mm
double image_height = 480.0; //Image Width: 640, Image Height: 480
double sensor_height = 5.8; //mm

double
humanDistance(double focal_length, double real_height, double image_height, double human_height,
              double sensor_height) {
    return ((focal_length * real_height * image_height) /
            (human_height * sensor_height * 1000.0)); //m
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

    face_cascade.detectMultiScale(frame_gray, faces, 1.1, 2, 0 | CV_HAAR_SCALE_IMAGE, Size(30, 30));

    for (size_t i = 0; i < faces.size(); i++) {
        Point center(faces[i].x + faces[i].width * 0.5, faces[i].y + faces[i].height * 0.5);
        ellipse(frame, center, Size(faces[i].width * 0.5, faces[i].height * 0.5), 0, 0, 360,
                Scalar(255, 0, 255), 4, 8, 0);

        Mat faceROI = frame_gray(faces[i]);
        vector <Rect> eyes;

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

void detectHuman(Mat &frame, double distance12, double distance23, double distance31) {
    HOGDescriptor hog;
    hog.setSVMDetector(HOGDescriptor::getDefaultPeopleDetector());

    vector <Rect> humans;
    Mat frame_gray;

    cvtColor(frame, frame_gray, CV_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);

    hog.detectMultiScale(frame_gray, humans, 0, Size(8, 8), Size(32, 32), 1.05, 2);

    vector <Rect> resRects;
    nms(humans, resRects, 0.65f);

    double distance_to_human;
    char str[500];

    for (int i = 0; i < resRects.size(); i++) {
        rectangle(frame, Point(resRects[i].x, resRects[i].y),
                  Point(resRects[i].x + resRects[i].width, resRects[i].y + resRects[i].height -
                                                           25), //(-25) Reason: Non-Maximum Suppression
                  CV_RGB((125 - i * 100) % 255, (i * 100) % 255, (255 - i * 100) % 255));

        double human_height = resRects[i].height - 25;
        distance_to_human = humanDistance(focal_length, real_height, image_height, human_height,
                                          sensor_height);

        __android_log_print(ANDROID_LOG_INFO, "Distances from Java Class", "distance12 = %f",
                            distance12);
        __android_log_print(ANDROID_LOG_INFO, "Distances from Java Class", "distance23 = %f",
                            distance23);
        __android_log_print(ANDROID_LOG_INFO, "Distances from Java Class", "distance31 = %f",
                            distance31);

        string dist_text = static_cast<ostringstream *>(&(ostringstream()
                << distance_to_human))->str();

        dist_text = dist_text.substr(0, 5);

        /*
        //320,240
        putText(frame, "Distance: " + dist_text + "m", Point(320 - 150, 240 - i * 50 - 20),
                CV_FONT_NORMAL, 0.5,
                CV_RGB((125 - i * 100) % 255, (i * 100) % 255, (255 - i * 100) % 255), 1, 1); */

        //640,480
        putText(frame, "Distance: " + dist_text + "m", Point(640 - 150, 480 - i * 50 - 20),
                CV_FONT_NORMAL, 0.5,
                CV_RGB((125 - i * 100) % 255, (i * 100) % 255, (255 - i * 100) % 255), 1, 1);
    }
}