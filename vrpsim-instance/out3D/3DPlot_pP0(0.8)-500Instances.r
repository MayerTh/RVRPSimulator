pP1 <- c(0.19999999999999996,0.18999999999999995,0.17999999999999997,0.16999999999999996,0.15999999999999995,0.14999999999999997,0.13999999999999996,0.12999999999999995,0.11999999999999995,0.10999999999999996,0.09999999999999996,0.08999999999999997,0.07999999999999997,0.06999999999999998,0.05999999999999997,0.04999999999999996,0.03999999999999995,0.029999999999999943,0.019999999999999934,0.009999999999999926)
pP2 <- c(0.0,0.01,0.02,0.03,0.04,0.05,0.060000000000000005,0.07,0.08,0.09,0.09999999999999999,0.10999999999999999,0.11999999999999998,0.12999999999999998,0.13999999999999999,0.15,0.16,0.17,0.18000000000000002,0.19000000000000003)
edod <- c(0.03363627999999999,0.03666473999999998,0.040448759999999986,0.04347144,0.04737279999999998,0.049752260000000006,0.05408473999999997,0.056192139999999995,0.06037519999999998,0.06339508,0.06649144000000003,0.07015856000000002,0.07288595999999997,0.07621253999999998,0.07852046,0.08322086000000004,0.08589025999999989,0.08957807999999987,0.09172021999999998,0.09629118000000003)
dod <- c(0.20065999999999984,0.20152000000000014,0.2018600000000001,0.20150000000000007,0.20107999999999995,0.19863999999999993,0.2009400000000002,0.19786000000000012,0.19920000000000004,0.20080000000000015,0.20020000000000018,0.2009599999999999,0.1984000000000001,0.1984200000000001,0.19828000000000015,0.19974000000000017,0.19784000000000015,0.19946000000000022,0.19768,0.19922000000000004)
s3d <- scatterplot3d(pP1, pP2, edod, color = "red", zlim=c(0,0,0.5))
s3d$points3d(pP1, pP2, dod, col = "green")
plot(pP2, edod, col = "red", ylim=c(0,1.0))
lines(pP2, dod, col = "green")