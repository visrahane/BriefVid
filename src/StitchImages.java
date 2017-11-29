import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class StitchImages {

	public static final File VIDEO_FILE = new File(
			"R:/Study/Masters/Fall 2017/CSCI-576 Multimedia/Project/CS576_Project_Videos/Apple.rgb");


	public static void main(String[] args) {
		// mergeImagesPanorama();

		pyramidBlending();
	}

	private static void pyramidBlending() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Mat A = Imgcodecs.imread("4.jpg");
		Mat B = Imgcodecs.imread("5.jpg");

		// gaussian pyramid for A
		Mat G = new Mat();
		Mat G1 = new Mat();
		A.copyTo(G);
		A.copyTo(G1);
		List<Mat> gpA = new ArrayList<>();
		gpA.add(G);
		for (int i = 0; i < 6; i++) {
			Mat GTemp = new Mat();
			Imgproc.pyrDown(G1, GTemp);
			GTemp.copyTo(G1);
			gpA.add(GTemp);
		}
		// gaussian pyramid for B
		Mat H = new Mat();
		Mat H1 = new Mat();
		B.copyTo(H);
		B.copyTo(H1);
		List<Mat> gpB = new ArrayList<>();
		gpB.add(H);
		for (int i = 0; i < 6; i++) {
			Mat HTemp = new Mat();
			// getPyramidDown(GTemp);
			Imgproc.pyrDown(H1, HTemp);
			HTemp.copyTo(H1);
			gpB.add(HTemp);
		}
		// generate Laplacian Pyramid for A
		List<Mat> lpA = new ArrayList<>();
		lpA.add(gpA.get(5));

		for (int i = 5; i > 0; i--) {
			Mat GE = new Mat(), L = new Mat();
			Imgproc.pyrUp(gpA.get(i), GE);
			Core.subtract(gpA.get(i-1), GE, L);
			lpA.add(L);
		}

		// generate Laplacian Pyramid for B
		List<Mat> lpB = new ArrayList<>();
		lpB.add(gpB.get(5));
		for (int i = 5; i > 0; i--) {
			Mat GE = new Mat(), L = new Mat();
			Imgproc.pyrUp(gpB.get(i), GE);
			Core.subtract(gpB.get(i - 1), GE, L);
			lpB.add(L);
		}

		// # Now add left and right halves of images in each level
		List<Mat> LS = new ArrayList<>();
		Mat la, lb, ls;
		for (int i = 0; i < lpA.size(); i++) {
			la = lpA.get(i);
			lb = lpB.get(i);
			int cols=la.cols();
			int rows=la.rows();
			int depth = la.depth();
			ls = getCombinedMat(cols / 2, la, lb);
			LS.add(ls);
		}

		Mat ls_=LS.get(0);
		for (int i = 1; i < 6; i++) {
			Imgproc.pyrUp(ls_, ls_);
			Core.add(ls_, LS.get(i), ls_);
		}
		Imgcodecs.imwrite("output.jpg", ls_);

	}

	private static Mat getCombinedMat(int cols, Mat la, Mat lb) {
		Mat img_left = la.submat(new Rect(0, 0, la.cols(), la.rows()));
		Mat img_right = lb.submat(new Rect(0, cols, lb.cols(), lb.rows()));
		Mat dst = new Mat();
		List<Mat> src = Arrays.asList(img_left, img_right);
		Core.hconcat(src, dst);
		return dst;
	}

	private static void mergeImagesPanorama() {
		System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		Mat img1 = Imgcodecs.imread("1.jpg");
		Mat img2 = Imgcodecs.imread("2.jpg");

		Mat gray_image1 = new Mat();
		Mat gray_image2 = new Mat();

		Imgproc.cvtColor(img1, gray_image1, Imgproc.COLOR_RGB2GRAY);
		Imgproc.cvtColor(img2, gray_image2, Imgproc.COLOR_RGB2GRAY);

		MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
		MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();

		FeatureDetector detector = FeatureDetector.create(FeatureDetector.SURF);
		detector.detect(gray_image1, keyPoints1);
		detector.detect(gray_image2, keyPoints2);

		Mat descriptors1 = new Mat();
		Mat descriptors2 = new Mat();

		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SURF);
		extractor.compute(gray_image1, keyPoints1, descriptors1);
		extractor.compute(gray_image2, keyPoints2, descriptors2);

		MatOfDMatch matches = new MatOfDMatch();

		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
		matcher.match(descriptors1, descriptors2, matches);

		double max_dist = 0;
		double min_dist = 100;
		List<DMatch> listMatches = matches.toList();

		for (int i = 0; i < listMatches.size(); i++) {
			double dist = listMatches.get(i).distance;
			if (dist < min_dist) {
				min_dist = dist;
			}
			if (dist > max_dist) {
				max_dist = dist;
			}
		}

		System.out.println("Min:" + min_dist);
		System.out.println("Max:" + max_dist);

		LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
		MatOfDMatch goodMatches = new MatOfDMatch();
		for (int i = 0; i < listMatches.size(); i++) {
			if (listMatches.get(i).distance < 2 * min_dist) {
				good_matches.addLast(listMatches.get(i));
			}
		}

		goodMatches.fromList(good_matches);

		Mat img_matches = new Mat(new Size(img1.cols() + img2.cols(), img1.rows()), CvType.CV_32FC2);

		LinkedList<Point> imgPoints1List = new LinkedList<Point>();
		LinkedList<Point> imgPoints2List = new LinkedList<Point>();
		List<KeyPoint> keypoints1List = keyPoints1.toList();
		List<KeyPoint> keypoints2List = keyPoints2.toList();

		for (int i = 0; i < good_matches.size(); i++) {
			imgPoints1List.addLast(keypoints1List.get(good_matches.get(i).queryIdx).pt);
			imgPoints2List.addLast(keypoints2List.get(good_matches.get(i).trainIdx).pt);
		}

		MatOfPoint2f obj = new MatOfPoint2f();
		obj.fromList(imgPoints1List);
		MatOfPoint2f scene = new MatOfPoint2f();
		scene.fromList(imgPoints2List);

		Mat H = Calib3d.findHomography(obj, scene, Calib3d.RANSAC, 3);

		Size s = new Size(img2.cols() + img1.cols(), img1.rows());

		Imgproc.warpPerspective(img1, img_matches, H, s);
		Mat m = new Mat(img_matches, new Rect(0, 0, img2.cols(), img2.rows()));

		img2.copyTo(m);

		Imgcodecs.imwrite("./out/out.jpg", img_matches);
	}

}
