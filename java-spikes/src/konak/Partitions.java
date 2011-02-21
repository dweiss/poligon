package konak;

import static java.lang.Math.*;

import java.awt.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Partitions
{
    public static class Tree
    {
        public double weight;
        public ArrayList<Tree> children = new ArrayList<Partitions.Tree>();
        public Polygon shape;

        public Tree(double weight)
        {
            this.weight = weight;
        }

        public Tree addChild(Tree t)
        {
            this.children.add(t);
            this.weight += t.weight;
            return this;
        }
    }

    public static class Point
    {
        public double x, y;

        public Point(double x, double y)
        {
            this.x = x;
            this.y = y;
        }

        public Point(Point point)
        {
            this.x = point.x;
            this.y = point.y;
        }
    }

    public static class Polygon
    {
        private ArrayList<Point> points_ = new ArrayList<Point>();

        public Polygon(double... points)
        {
            for (int i = 0; i < points.length; i += 2)
                this.points_.add(new Point(points[i], points[i + 1]));
        }

        public Polygon clone()
        {
            Polygon p = new Polygon();
            for (int i = 0; i < points_.size(); i++)
            {
                p.points_.add(new Point(points_.get(i)));
            }
            return p;
        }

        public int size()
        {
            return points_.size();
        }
        
        public Point get(int i)
        {
            return points_.get(i);
        }

        public void clear()
        {
            this.points_.clear();
        }
        
        public void add(Point p)
        {
            this.points_.add(p);
        }
    }

    public static interface IPartitionStrategy
    {
        public Pair<Polygon, Polygon> split(Polygon p, double part);
    }

    public static class GreedyCut implements IPartitionStrategy
    {
        public Pair<Polygon, Polygon> split(Polygon p, double part)
        {
            Pair<Polygon, Polygon> result = new Pair<Polygon, Polygon>();
            double aspectRatio = -1;
            for (int i = 0; i < 360; i++)
            {
                double angle = ((double) i) / 360 * 2 * PI;
                Polygon local = p.clone();

                rotatePoly(local, angle);
                Pair<Polygon, Polygon> pair = horizontalCut(local, part);
                double localAspect = max(aspect(pair.first), aspect(pair.second));

                if (aspectRatio == -1 || localAspect < aspectRatio)
                {
                    aspectRatio = localAspect;
                    result.first = pair.first;
                    result.second = pair.second;
                    rotatePoly(result.first, -angle);
                    rotatePoly(result.second, -angle);
                }
            }

            return result;
        }
    }

    private final static double PRECISION = 1e-10;

    private static boolean differentPoints(Point p1, Point p2)
    {
        return (dist(p1, p2) > PRECISION);
    }

    private static double standardDirection(Point p1, Point p2)
    {
        if (p1.y > p2.y)
        {
            Point t = p1;
            p1 = p2;
            p2 = t;
        }

        return acos((p2.x - p1.x) / dist(p1, p2));
    }

    public static class SplitWidestAngle implements IPartitionStrategy
    {
        public Pair<Polygon, Polygon> split(Polygon p, double part)
        {
            double angle = splitWidestAngle(p);
            Polygon local = p.clone();
            rotatePoly(local, -angle);
            Pair<Polygon, Polygon> pair = horizontalCut(local, part);
            rotatePoly(pair.first, angle);
            rotatePoly(pair.second, angle);

            return pair;
        }

        private static double splitWidestAngle(Polygon p)
        {
            ArrayList<Double> angles = new ArrayList<Double>();

            for (int i = 0; i < p.size(); i++)
                if (differentPoints(p.get(i),
                    p.get((i + 1) % p.size()))) angles
                    .add(standardDirection(p.get(i),
                        p.get((i + 1) % p.size())));

            Collections.sort(angles);

            double maxdiff = -1;
            double bestangle = 0;

            for (int i = 0; i < angles.size(); i++)
            {
                double diff = angles.get((i + 1) % angles.size()) - angles.get(i);
                if (diff < 0) diff += PI;
                if (diff > maxdiff)
                {
                    maxdiff = diff;
                    bestangle = angles.get(i) + diff / 2;
                }
            }
            return bestangle;
        }
    }

    public static class FirstPowerOf2 implements IPartitionStrategy
    {
        private static double findAngleMethod3(Polygon p)
        {
            ArrayList<Double> angles = new ArrayList<Double>();

            for (int i = 0; i < p.size(); i++)
                if (differentPoints(p.get(i),
                    p.get((i + 1) % p.size()))) angles
                    .add(standardDirection(p.get(i),
                        p.get((i + 1) % p.size())));

            double step = PI / 2;
            int attempts = 2;
            for (;;)
            {
                for (int i = 0; i < attempts; i++)
                {
                    double mindiff = 2 * PI;
                    double now = i * step;
                    for (int j = 0; j < angles.size(); j++)
                    {
                        mindiff = min(mindiff, abs((angles.get(j) - PI) - now));
                        mindiff = min(mindiff, abs(angles.get(j) - now));
                        mindiff = min(mindiff, abs((angles.get(j) + PI) - now));
                    }
                    if (mindiff >= step - 1e-5) return now;
                }
                step = step / 2;
                attempts *= 2;
            }
        }

        public Pair<Polygon, Polygon> split(Polygon p, double part)
        {
            double angle = findAngleMethod3(p);
            Polygon local = p.clone();
            rotatePoly(local, -angle);
            Pair<Polygon, Polygon> pair = horizontalCut(local, part);
            rotatePoly(pair.first, angle);
            rotatePoly(pair.second, angle);
            return pair;
        }
    }

    public static class RandomPowerOf2 implements IPartitionStrategy
    {
        private static double findAngleMethod4(Polygon p)
        {
            ArrayList<Double> angles = new ArrayList<Double>();

            for (int i = 0; i < p.size(); i++)
                if (differentPoints(p.get(i),
                    p.get((i + 1) % p.size()))) angles
                    .add(standardDirection(p.get(i),
                        p.get((i + 1) % p.size())));

            double step = PI / 2;
            int attempts = 2;
            for (;;)
            {
                ArrayList<Double> options = new ArrayList<Double>();
                for (int i = 0; i < attempts; i++)
                {
                    double mindiff = 2 * PI;
                    double now = i * step;
                    for (int j = 0; j < angles.size(); j++)
                    {
                        mindiff = min(mindiff, abs((angles.get(j) - PI) - now));
                        mindiff = min(mindiff, abs(angles.get(j) - now));
                        mindiff = min(mindiff, abs((angles.get(j) + PI) - now));
                    }
                    if (mindiff >= step - 1e-5) options.add(now);
                }
                if (!options.isEmpty()) return options.get(new Random().nextInt(options.size()));
                step = step / 2;
                attempts *= 2;
            }
        }

        public Pair<Polygon, Polygon> split(Polygon p, double part)
        {
            double angle = findAngleMethod4(p);
            Polygon local = p.clone();
            rotatePoly(local, -angle);
            Pair<Polygon, Polygon> pair = horizontalCut(local, part);
            rotatePoly(pair.first, angle);
            rotatePoly(pair.second, angle);
            return pair;
        }
    }
    
    public static class RandomCut implements IPartitionStrategy
    {
        public Pair<Polygon, Polygon> split(Polygon p, double part)
        {
            double angle = 2 * PI * (random());
            Polygon local = p.clone();
            rotatePoly(local, -angle);
            Pair<Polygon, Polygon> pair = horizontalCut(local, part);
            rotatePoly(pair.first, angle);
            rotatePoly(pair.second, angle);
            return pair;
        }
    }

    private IPartitionStrategy partitionStrategy = new SplitWidestAngle();

    public void partition(Tree t, Polygon p)
    {
        if (t.children.isEmpty())
        {
            // Leaf node.
        }
        else
        {
            outputPartition(t.children, 0, t.children.size() - 1, p);
        }

        t.shape = p;
    }

    private void outputPartition(ArrayList<Tree> v, int a, int b, Polygon p)
    {
        if (a == b)
        {
            // descend one level down.
            partition(v.get(a), p);
        }
        else
        {
            int c = a + (b - a + 1) / 2;
            double part = sumWeights(v, a, c - 1) / sumWeights(v, a, b);
            if (sumWeights(v, a, c - 1) == 0)
            {
                outputPartition(v, c, b, p);
            }
            else if (sumWeights(v, c, b) == 0)
            {
                outputPartition(v, a, c - 1, p);
            }
            else
            {
                Pair<Polygon, Polygon> result = partitionStrategy.split(p, part);
                outputPartition(v, a, c - 1, result.first);
                outputPartition(v, c, b, result.second);
            }
        }
    }

    private static void ySymmetryPoly(Polygon p)
    {
        for (int i = 0; i < p.size(); i++)
            p.get(i).y *= -1;
    }

    private static void horizontalCutAt(Polygon p, double y, Polygon np)
    {
        int n = p.size();
        np.clear();

        for (int i = 0; i < n; i++)
        {
            double y1 = p.get(i).y, 
                   x1 = p.get(i).x;

            double y2 = p.get((i + 1) % n).y, 
                   x2 = p.get((i + 1) % n).x;

            if (y1 <= y) np.add(new Point(p.get(i)));
            if (max(y1, y2) > y && min(y1, y2) < y)
            {
                double u = (y - y1) / (y2 - y1);
                np.add(new Point(x1 + u * (x2 - x1), y));
            }
        }
    }

    private static Pair<Polygon, Polygon> horizontalCut(Polygon p, double part)
    {
        ArrayList<Double> ys = new ArrayList<Double>();
        for (int i = 0; i < p.size(); i++)
            ys.add(p.get(i).y);
        Collections.sort(ys);

        double totalArea = area(p);
        double prevY = ys.get(0);
        double prevArea = 0;

        Polygon p1 = new Polygon();
        Polygon p2 = new Polygon();
        for (int i = 1; i <= ys.size(); i++)
        {
            double currentY = ys.get(i);
            horizontalCutAt(p, currentY, p1);

            double currentArea = area(p1);
            if (part <= currentArea / totalArea)
            {
                horizontalCutAt(p, (currentY + prevY) / 2, p1);
                double midArea = area(p1);
                double c = (midArea - prevArea) / (currentArea - prevArea);
                double a = 2 - 4 * c;
                double b = 1 - a;
                double u;
                if (abs(a) < 1e-6)
                {
                    u = (part * totalArea - prevArea) / (currentArea - prevArea);
                }
                else
                {
                    double d = (part * totalArea - prevArea) / (currentArea - prevArea);
                    u = (-b - sqrt(b * b + 4 * a * d)) / 2 / a;
                    if (u < 0 || u > 1) u = (-b + sqrt(b * b + 4 * a * d)) / 2 / a;
                }
                double y = prevY + u * (currentY - prevY);
                horizontalCutAt(p, y, p1);
                Polygon copy = p.clone();
                ySymmetryPoly(copy);
                horizontalCutAt(copy, -y, p2);
                ySymmetryPoly(p2);
                break;
            }
            prevY = currentY;
            prevArea = currentArea;
        }

        return new Pair<Polygon, Polygon>(p1, p2);
    }

    private static void rotatePoly(Polygon p, double angle)
    {
        for (int i = 0; i < p.size(); i++)
            rotate(p.get(i), angle);
    }

    private static void rotate(Point p, double angle)
    {
        double nx = p.x * cos(angle) - p.y * sin(angle);
        double ny = p.x * sin(angle) + p.y * cos(angle);
        p.x = nx;
        p.y = ny;
    }

    private static double aspect(Polygon p)
    {
        double d = diam(p);
        return d * d / area(p);
    }

    private static double area(Point p1, Point p2, Point p3)
    {
        return abs((p2.x - p1.x) * (p3.y - p1.y) - (p2.y - p1.y) * (p3.x - p1.x)) / 2;
    }

    private static double area(Polygon p)
    {
        double sum = 0;
        for (int i = 1; i <= p.size() - 2; i++)
        {
            sum += area(p.get(0), p.get(i), p.get(i + 1));
        }
        return sum;
    }

    private static double diam(Polygon p)
    {
        double diam = 0;
        for (int i = 0; i < p.size(); i++)
            for (int j = 0; j < p.size(); j++)
                diam = max(diam, dist(p.get(i), p.get(j)));
        return diam;
    }

    private static double dist(Point p1, Point p2)
    {
        double one = p1.x - p2.x;
        double two = p1.y - p2.y;
        return sqrt(one * one + two * two);
    }

    private static double sumWeights(ArrayList<Tree> vec, int a, int b)
    {
        double sum = 0;
        for (int i = a; i <= b; i++)
        {
            sum += vec.get(i).weight;
        }
        return sum;
    }

    public static void main(String [] args)
    {
        final Tree t = new Tree(0);

        t.addChild(new Tree(30))
            .addChild(new Tree(30))
            .addChild(
                new Tree(0).addChild(new Tree(15)).addChild(
                    new Tree(0).addChild(new Tree(5)).addChild(new Tree(5))
                        .addChild(new Tree(5))));

        new Partitions().partition(t, new Polygon(10, 10, 10, 760, 760, 760, 760, 10));

        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel comp = new JPanel()
        {
            @Override
            public void paint(Graphics g)
            {
                paint((Graphics2D) g, t, 1);
            }

            private void paint(Graphics2D g, Tree t, int stroke)
            {
                for (int i = 0; i < t.shape.size(); i++)
                {
                    Point s = t.shape.get(i);
                    Point e = t.shape.get((i + 1) % t.shape.size());
                    g.setStroke(new BasicStroke(Math.max(1, 10 - stroke)));
                    AlphaComposite ac = AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, 0.5f);
                    g.setComposite(ac);
                    g.drawLine((int) s.x, (int) s.y, (int) e.x, (int) e.y);
                }

                for (Tree child : t.children)
                    paint(g, child, stroke + 1);
            }
        };
        f.getContentPane().add(comp);
        f.setVisible(true);
        f.setSize(800, 800);
    }
}
