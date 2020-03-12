def viewTransform(p : Tuple3[Double, Double, Double]) : Tuple3[Double, Double, Double] = {
    var x = p._1 - camX;
    var y = p._2 - camY;
    var z = p._3 - camZ;

    val z2 = x * Math.cos(camRY) + z * Math.sin(camRY);
    x = z * Math.cos(camRY) - x * Math.sin(camRY);
    z = z2 * Math.cos(camRX) - y * Math.sin(camRX);
    y = z2 * Math.sin(camRX) + y * Math.cos(camRX);

    if(z == 0)
        z = 0.0000001;
    x *= zScale / z;
    y *= zScale / z;

    return Tuple3(x, y, z);
}

val points = Array[Tuple3[Double, Double, Double]](
    Tuple3(0, 0, 0),
    Tuple3(0, 0, 1),
    Tuple3(0, 1, 0),
    Tuple3(0, 1, 1),
    Tuple3(1, 0, 0),
    Tuple3(1, 0, 1),
    Tuple3(1, 1, 0),
    Tuple3(1, 1, 1)
);
val numPoints = points.length;
val pPoints = Array.fill[Picture](points.length)(
    Picture {
            setPenColor(noColor);
            setFillColor(black);
            circle(9);
    }
);

val lines = Array(
    Tuple2(0, 1),
    Tuple2(0, 2),
    Tuple2(1, 3),
    Tuple2(2, 3),
    Tuple2(0, 4),
    Tuple2(1, 5),
    Tuple2(2, 6),
    Tuple2(3, 7),
    Tuple2(4, 5),
    Tuple2(4, 6),
    Tuple2(5, 7),
    Tuple2(6, 7),
);
val pLines = Array.fill[Picture](lines.length)(Picture {});

var camX : Double = 0.5;
var camY : Double = 0.5;
var camZ : Double = -3;
var camRX : Double = 0;
var camRY : Double = Math.PI / 2;

val moveSpeed = 0.06;
val turnSpeed = 0.04;
val zScale = 500;

setup {
    clear();
    clearOutput();
    setSlowness(0);
    setRefreshRate(60);
    setBackground(white);
    invisible();

    pPoints.foreach { draw(_) };
}

drawLoop {
    if(isKeyPressed(Kc.VK_L))
        camRY += turnSpeed;
    if(isKeyPressed(Kc.VK_H))
        camRY -= turnSpeed;
    if(isKeyPressed(Kc.VK_K))
        camRX -= turnSpeed;
    if(isKeyPressed(Kc.VK_J))
        camRX += turnSpeed;

    camRX = Math.max(Math.PI * -0.5, Math.min(Math.PI * 0.5, camRX));

    if(isKeyPressed(Kc.VK_W)) {
        camZ += moveSpeed * Math.sin(camRY);
        camX += moveSpeed * Math.cos(camRY);
    }
    if(isKeyPressed(Kc.VK_S)) {
        camZ -= moveSpeed * Math.sin(camRY);
        camX -= moveSpeed * Math.cos(camRY);
    }
    if(isKeyPressed(Kc.VK_A)) {
        camZ -= moveSpeed * Math.cos(camRY);
        camX += moveSpeed * Math.sin(camRY);
    }
    if(isKeyPressed(Kc.VK_D)) {
        camZ += moveSpeed * Math.cos(camRY);
        camX -= moveSpeed * Math.sin(camRY);
    }
    if(isKeyPressed(Kc.VK_E))
        camY += moveSpeed;
    if(isKeyPressed(Kc.VK_Q))
        camY -= moveSpeed;

    for(i <- 0 to points.length - 1) {  
        val dp = viewTransform(points(i));
        if(dp._3 > 0)
            pPoints(i).setPosition(dp._1 + 9, dp._2);
        else
            pPoints(i).setPosition(cwidth, cheight);
    }

    for(i <- 0 to lines.length - 1) {
        var p1 = viewTransform(points(lines(i)._1));
        var p2 = viewTransform(points(lines(i)._2));
        pLines(i).erase();
        if(p1._3 > 0 || p2._3 > 0) {
            if(p1._3 < 0 || p2._3 < 0) {
                if(p2._3 < 0) {
                    val swp = p1;
                    p1 = p2;
                    p2 = swp;
                }
                val f: Double = p1._3 / (p2._3 - p1._3);
                p1 = Tuple3(
                    (p1._1 * f - p2._1 * (f - 1)) / (p2._3 - p1._3),
                    (p1._2 * f - p2._2 * (f - 1)) / (p2._3 - p1._3),
                    0
                );
            }
            pLines(i) = Picture {
                setPenColor(black);
                setPenThickness(5);
                jumpTo(p1._1, p1._2);
                lineTo(p2._1, p2._2);
            };
            draw(pLines(i));
        }
    }
}