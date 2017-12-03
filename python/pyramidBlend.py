import cv2
import numpy as np,sys
#A = cv2.imread(sys.argv[1])
#B = cv2.imread(sys.argv[2])
A = cv2.imread("1.jpg")
B = cv2.imread("2.jpg")
# generate Gaussian pyramid for A
G = A.copy()
gpA = [G]
for i in range(6):
   G = cv2.pyrDown(G)
   gpA.append(G)

G = B.copy()
gpB = [G]
for i in range(6):
   G = cv2.pyrDown(G)
   gpB.append(G)

lpA = [gpA[5]]
for i in range(5,0,-1):
   GE = cv2.pyrUp(gpA[i])
   if (len(gpA[i-1])<len(GE)):
      np.pad(gpA[i - 1], (0, len(GE)-len(gpA[i - 1]) ), 'constant')
   elif(len(gpA[i-1])>len(GE)):
      np.pad(GE, (0, len(gpA[i-1])-len(GE)), 'constant')

   print(sys.getsizeof(GE),sys.getsizeof(gpA[i-1]))
   L = cv2.subtract(gpA[i - 1], GE)
   lpA.append(L)

lpB = [gpB[5]]
for i in range(5,0,-1):
   GE = cv2.pyrUp(gpB[i])
   L = cv2.subtract(gpB[i - 1], GE)
   lpB.append(L)

LS = []
for la,lb in zip(lpA,lpB):
   rows, cols, dpt = la.shape
   ls = np.hstack((la[:, 0:int(cols / 2)], lb[:, int(cols / 2):]))
   LS.append(ls)

ls_ = LS[0]
for i in range(1,6):
   ls_ = cv2.pyrUp(ls_)
   ls_ = cv2.add(ls_, LS[i])

real = np.hstack((A[:,:int(cols/2)],B[:,int(cols/2):]))
cv2.imwrite('finalTapestry.jpg',ls_)
#cv2.imwrite('Direct_blending.jpg', real)