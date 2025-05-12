package yigame;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class MyPoint {
    int x;
    int y;

    public MyPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class MyCircle {
    MyPoint p;
    int r;
    Color color;
    Color originalColor; // 新增的變量來存儲原本的顏色
    String str;
    boolean isSelected; // 新增的布爾變量

    public MyCircle(MyPoint p, int r, Color color, String str) {
        this.p = p;
        this.r = r;
        this.color = color;
        this.str = str;
        this.isSelected = false; // 初始化為未選中
    }
}

class MyGame {
    static final int Cn = 61 + 25 * 6;
    MyCircle[] A = new MyCircle[Cn];
    int[][] Ac = new int[Cn][8];
}

public class yigame extends JPanel {
    MyGame game = new MyGame();
    MyCircle selectedCircle = null; // 記錄當前選中的圓形
    boolean isGameOver = false;
    int[][] pair = { { 74, 10 }, { 75, 25 }, { 82, 17 }, { 100, 1 }, { 101, 3 }, { 108, 2 }, { 120, 5 }, { 127, 18 },
            { 135, 11 }, { 145, 50 }, { 146, 35 }, { 153, 43 }, { 171, 59 }, { 172, 57 }, { 179, 58 }, { 198, 42 },
            { 199, 55 }, { 206, 49 } };

    public yigame() {
        init(game, 400, 300, 30, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isGameOver) {
                    return; // 如果遊戲已經結束，不執行任何操作
                }

                MyPoint clickPoint = new MyPoint(e.getX(), e.getY());
                for (MyCircle circle : game.A) {
                    if (circle != null && inSide(circle, clickPoint)) {
                        // 找到點擊的圓形
                        int currentIndex = findCircleIndex(circle);

                        // 打印當前點及其鄰居
                        System.out.println("Selelected Point " + currentIndex);
                        System.out.print("Neighbors: ");
                        for (int neighbor : game.Ac[currentIndex]) {
                            if (neighbor > 0) { // 確保有鄰居
                                System.out.print((neighbor - 1) + " "); // 打印鄰居的 0-based 索引
                            }
                        }
                        System.out.println();
                        System.out.println("====================================");
                        
                        if (selectedCircle != null) {
                            // 恢復之前選中的圓形顏色
                            selectedCircle.isSelected = false;
                            selectedCircle.color = selectedCircle.originalColor;
                        }

                        // 如果點擊的是一個有效的旗子
                        if (circle.originalColor != Color.WHITE) {
                            // 設定新選中的圓形顏色為黃色
                            circle.isSelected = true;
                            circle.color = Color.YELLOW;
                            selectedCircle = circle; // 更新選中的圓形
                            repaint();
                        } else if (selectedCircle != null) {
                            // 確保目標點是相鄰點
                            currentIndex = findCircleIndex(selectedCircle);
                            int targetIndex = findCircleIndex(circle);
                            if (isAdjacent(currentIndex, targetIndex)) {
                                // 點擊空白位置，移動旗子
                                circle.color = selectedCircle.originalColor; // 將顏色設為選中的旗子的顏色
                                circle.originalColor = selectedCircle.originalColor;

                                // 將之前的旗子位置設為白色
                                selectedCircle.color = Color.WHITE;
                                selectedCircle.originalColor = Color.WHITE;
                                selectedCircle.isSelected = false;
                                selectedCircle = null; // 清除選中的旗子
                                repaint();

                                // 檢查國王棋子是否已經走到特定顏色的王位
                                if ((circle.color.equals(Color.GREEN) && targetIndex == 161) ||
                                    (circle.color.equals(Color.RED) && targetIndex == 186) ||
                                    (circle.color.equals(Color.BLUE) && targetIndex == 136)) {
                                    String colorName = circle.color.equals(Color.GREEN) ? "綠色" :
                                                       circle.color.equals(Color.RED) ? "紅色" :
                                                       "藍色";
                                    JOptionPane.showMessageDialog(null, colorName + "國王棋子已經走到王位！");
                                    isGameOver = true; // 設置遊戲結束狀態
                                    JOptionPane.showMessageDialog(null, "遊戲結束！請關閉視窗重新開始遊戲。");
                                }
                            }
                        } else {
                            repaint();
                        }
                        break;
                    }
                }
            }

            private int findCircleIndex(MyCircle circle) {
                for (int i = 0; i < game.A.length; i++) {
                    if (game.A[i] == circle) {
                        return i;
                    }
                }
                return -1; // 未找到
            }

            private boolean isAdjacent(int currentIndex, int targetIndex) {
                if (currentIndex == -1 || targetIndex == -1) {
                    return false;
                }
                for (int neighbor : game.Ac[currentIndex]) {
                    if (neighbor - 1 == targetIndex) { // 鄰接矩陣是 1-based 索引
                        return true;
                    }
                }
                return false;
            }            
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Set<String> drawnLines = new HashSet<>();
        int k = 0;
        for (int i = 0; i < MyGame.Cn; i++) {
            if (k < pair.length && pair[k][0] == i) {
                k++;
                continue;
            }
            for (int j = 0; j < 8; j++) {
                if (game.Ac[i][j] > 0) {
                    String lineKey = i + "-" + game.Ac[i][j];
                    String reverseLineKey = game.Ac[i][j] + "-" + i;
                    if (!drawnLines.contains(lineKey) && !drawnLines.contains(reverseLineKey)) {
                        g2d.drawLine(game.A[i].p.x, game.A[i].p.y, game.A[game.Ac[i][j] - 1].p.x, game.A[game.Ac[i][j] - 1].p.y);
                        drawnLines.add(lineKey);
                    }
                }
            }
        }
        int[] skipIndices = { 74, 75, 82, 100, 101, 108, 120, 127, 135, 145, 146, 153, 171, 172, 179, 198, 199, 206 };
        Set<Integer> skipSet = new HashSet<>();
        for (int index : skipIndices) {
            skipSet.add(index);
        }
        for (int i = 0; i < MyGame.Cn; i++) {
            if (game.A[i] != null && !skipSet.contains(i)) {
                drawCircle(g2d, game.A[i]);
            }
        }
    }

    void drawCircle(Graphics2D g2d, MyCircle c) {
        g2d.setColor(c.color);
        g2d.fill(new Ellipse2D.Double(c.p.x - c.r, c.p.y - c.r, 2 * c.r, 2 * c.r));
        g2d.setColor(Color.BLACK);
        g2d.draw(new Ellipse2D.Double(c.p.x - c.r, c.p.y - c.r, 2 * c.r, 2 * c.r));
        g2d.drawString(c.str, c.p.x - c.r + 5, c.p.y - c.r + 2);
    }

    private Color getColorByNumber(int number) {
        int[] greenNumbers = { 1, 2, 3, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107,
                108, 109, 110 };
        int[] greenKingNumbers = { 86 };
        int[] blueNumbers = { 10, 17, 25, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82,
                83, 84, 85 };
        int[] blueKingNumbers = { 61 };
        int[] redNumbers = { 5, 11, 18, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129,
                130, 131, 132, 133, 134, 135 };
        int[] redKingNumbers = { 111 };

        for (int num : greenNumbers) {
            if (number == num) {
                return new Color(50, 200, 100);
            }
        }

        for (int num : greenKingNumbers) {
            if (number == num) {
                return Color.GREEN;
            }
        }

        for (int num : blueNumbers) {
            if (number == num) {
                return new Color(50, 100, 200);
            }
        }

        for (int num : blueKingNumbers) {
            if (number == num) {
                return Color.BLUE;
            }
        }

        for (int num : redNumbers) {
            if (number == num) {
                return new Color(200, 50, 100);
            }
        }

        for (int num : redKingNumbers) {
            if (number == num) {
                return Color.RED;
            }
        }

        return Color.WHITE;
    }

    boolean inSide(MyCircle c, MyPoint p) {
        return ((c.p.x - p.x) * (c.p.x - p.x) + (c.p.y - p.y) * (c.p.y - p.y)) < c.r * c.r;
    }

    public void init(MyGame g, int X0, int Y0, int d, int r) {
        ////// 六角形
        double theta = Math.PI / 3;
        int P0x = X0 - 4 * d;
        int P0y = Y0 + (int) (4 * d * Math.sin(theta));
        int i, s, n, j, x0, y0, left = 0, right = 0, index = 0;

        for (i = 0; i < 9; i++) {
            s = Math.abs(i - 4);
            n = 9 - s;
            x0 = P0x + s * d / 2;
            y0 = (int) (P0y - i * d * Math.sin(theta));
            right += n;
            for (j = 0; j < n; j++) {
                g.A[index] = new MyCircle(new MyPoint(x0 + d * j, y0), r, getColorByNumber(index), String.valueOf(index));
                g.A[index].originalColor = g.A[index].color;

                if (index > left)
                    g.Ac[index][0] = index;
                if (index < right - 1)
                    g.Ac[index][1] = index + 2;
                if (!(index <= 4 || (index == left && index <= 26))) {
                    if (index <= 34)
                        g.Ac[index][2] = index - n + 1;
                    else
                        g.Ac[index][2] = index - n;
                }
                if (!(index <= 4 || (index == right && index >= 26))) {
                    if (index <= 34)
                        g.Ac[index][3] = index - n + 2;
                    else
                        g.Ac[index][3] = index - n + 1;
                }
                if (!(index >= 56 || (index == left && index >= 26))) {
                    if (index >= 26)
                        g.Ac[index][4] = index + n;
                    else
                        g.Ac[index][4] = index + n + 1;
                }
                if (!(index >= 56 || (index == right - 1 && index >= 34))) {
                    if (index >= 26)
                        g.Ac[index][5] = index + n + 1;
                    else
                        g.Ac[index][5] = index + n + 2;
                }
                index++;
                System.out.println("Point " + (index - 1) + " coordinates: (" + g.A[index - 1].p.x + ", "
                        + g.A[index - 1].p.y + ")");
            }
            System.out.println("====================================");
            left += n;
        }

        ////// 六個圓形
        int R1 = d + 5;
        int R2 = (int) (d / Math.sin(Math.PI / 8));
        int R = (int) (R2 * Math.cos(Math.PI / 8) + 4 * d * Math.sin(theta));
        theta = Math.PI / 3;
        double theta0 = Math.PI / 6;
        double[] theta2 = { Math.PI / 24, Math.PI / 8, -Math.PI / 24, Math.PI / 24, Math.PI / 8, -Math.PI / 24 };

        for (i = 0; i < 6; i++) {
            x0 = (int) (X0 + R * Math.cos(theta * i + theta0));
            y0 = (int) (Y0 + R * Math.sin(theta * i + theta0));
            g.A[index] = new MyCircle(new MyPoint(x0, y0), r, getColorByNumber(index), String.valueOf(index));
            g.A[index].originalColor = g.A[index].color;
            int center = index;
            index++;
            double theta1 = Math.PI / 4;
            for (j = 0; j < 8; j++) {
                g.A[index] = new MyCircle(new MyPoint((int) (x0 + R1 * Math.cos(theta1 * j + theta2[i])),
                        (int) (y0 + R1 * Math.sin(theta1 * j + theta2[i]))),
                        r, getColorByNumber(index), String.valueOf(index));
                        g.A[index].originalColor = g.A[index].color;
                index++;
                System.out.println("Point " + (index - 1) + " coordinates: (" + g.A[index - 1].p.x + ", "
                        + g.A[index - 1].p.y + ")");
                g.Ac[center][j] = index;
                g.Ac[index - 1][0] = center + 1;
            }
            System.out.println("====================================");
            for (j = 0; j < 8; j++) {
                g.Ac[center + 1 + j][1] = center + 2 + (j + 1) % 8;
                g.Ac[center + 1 + j][2] = center + 2 + (j + 7) % 8;
                g.Ac[center + 1 + j][3] = center + j + 8 + 1 + 1;
                g.Ac[center + 1 + j][4] = center + j + 15 + 2;
                if (j == 0)
                    g.Ac[center + 1 + j][5] = center + j + 24 + 1;
                else
                    g.Ac[center + 1 + j][5] = center + j + 16 + 1;
            }
            int start = index;
            for (j = 0; j < 8; j++) {
                g.A[index] = new MyCircle(new MyPoint((int) (x0 + R2 * Math.cos(theta1 * j + theta2[i])),
                        (int) (y0 + R2 * Math.sin(theta1 * j + theta2[i]))),
                        r, getColorByNumber(index), String.valueOf(index));
                        g.A[index].originalColor = g.A[index].color;
                g.Ac[index][0] = index - 8 + 1;
                index++;
                System.out.println("Point " + (index - 1) + " coordinates: (" + g.A[index - 1].p.x + ", "
                        + g.A[index - 1].p.y + ")");
            }
            System.out.println("====================================");
            for (j = 0; j < 8; j++) {
                g.Ac[start + j][1] = start + j + 7 + 1;
                g.Ac[start + j][2] = start + j + 8 + 1;
            }
            for (j = 0; j < 8; j++) {
                g.A[index] = new MyCircle(new MyPoint(
                        (g.A[start + j].p.x + g.A[start + (j + 1) % 8].p.x) / 2,
                        (g.A[start + j].p.y + g.A[start + (j + 1) % 8].p.y) / 2),
                        r, getColorByNumber(index), String.valueOf(index));
                        g.A[index].originalColor = g.A[index].color;
                if (j < 7) {
                    g.Ac[index][0] = index - 15 + 1;
                    g.Ac[index][1] = index - 16 + 1;
                    g.Ac[index - 15][4] = index + 1;
                    g.Ac[index - 16][4] = index + 1;
                    g.Ac[index][2] = index - 7 + 1;
                    g.Ac[index][3] = index - 8 + 1;
                    g.Ac[index - 7][5] = index + 1;
                    g.Ac[index - 8][5] = index + 1;
                } else {
                    g.Ac[index][0] = index - 23 + 1;
                    g.Ac[index][1] = index - 16 + 1;
                    g.Ac[index - 23][4] = index + 1;
                    g.Ac[index - 16][4] = index + 1;
                    g.Ac[index][2] = index - 15 + 1;
                    g.Ac[index][3] = index - 8 + 1;
                    g.Ac[index - 15][5] = index + 1;
                    g.Ac[index - 8][5] = index + 1;
                }
                index++;
                System.out.println("Point " + (index - 1) + " coordinates: (" + g.A[index - 1].p.x + ", "
                        + g.A[index - 1].p.y + ")");
            }
            System.out.println("====================================");
        }

        // 建立配對的映射表（僅將第一個數字映射到第二個數字）
        Map<Integer, Integer> pairMap = new HashMap<>();
        for (int[] p : pair) {
            pairMap.put(p[0], p[1]); // 僅記錄第一格到第二格的映射
        }

        // 遍歷所有點的鄰居
        for (i = 0; i < g.Ac.length; i++) {
            for (j = 0; j < 8; j++) {
                if (g.Ac[i][j] > 0) {
                    int neighbor = g.Ac[i][j] - 1; // 1-based 索引轉換為 0-based
                    if (pairMap.containsKey(neighbor)) {
                        // 如果鄰居是配對的第一個數字，替換成第二個數字
                        g.Ac[i][j] = pairMap.get(neighbor) + 1; // 轉換回 1-based 索引
                    }
                }
            }
        }

        // 合併鄰居
        for (int[] p : pair) {
            int source = p[0]; // 第一個數字
            int target = p[1]; // 第二個數字

            // 合併 `source` 的鄰居到 `target`
            Set<Integer> neighbors = new HashSet<>();
            for (j = 0; j < 8; j++) {
                if (g.Ac[target][j] > 0) {
                    neighbors.add(g.Ac[target][j] - 1); // 1-based 索引轉換為 0-based
                }
                if (g.Ac[source][j] > 0) {
                    neighbors.add(g.Ac[source][j] - 1);
                }
            }

            // 將合併後的鄰居重新寫回 `target`
            int k = 0;
            for (int neighbor : neighbors) {
                g.Ac[target][k++] = neighbor + 1; // 轉換回 1-based 索引
                if (k >= 8)
                    break; // 最多支持 8 個鄰居
            }

            // 清空 `source` 的鄰居
            for (j = 0; j < 8; j++) {
                g.Ac[source][j] = 0;
            }
        }
    }

    void draw(Graphics2D g2d, MyGame g) {
        Set<String> drawnLines = new HashSet<>();
        int k = 0;
        for (int i = 0; i < MyGame.Cn; i++) {
            if (k < pair.length && pair[k][0] == i) {
                k++;
                continue;
            }
            for (int j = 0; j < 8; j++) {
                if (g.Ac[i][j] > 0) {
                    String lineKey = i + "-" + g.Ac[i][j];
                    String reverseLineKey = g.Ac[i][j] + "-" + i;
                    if (!drawnLines.contains(lineKey) && !drawnLines.contains(reverseLineKey)) {
                        g2d.drawLine(g.A[i].p.x, g.A[i].p.y, g.A[g.Ac[i][j] - 1].p.x, g.A[g.Ac[i][j] - 1].p.y);
                        drawnLines.add(lineKey);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("yigame");
        yigame board = new yigame();
        frame.add(board);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // 將視窗設置在螢幕中央
        frame.setVisible(true);
        // 顯示遊戲規則
        String gameRules = "遊戲規則：\n"
                + "1. 每個玩家輪流移動一個棋子。\n"
                + "2. 棋子只能移動到相鄰的空格。\n"
                + "3. 綠色國王棋子應該走到位置 161。\n"
                + "4. 紅色國王棋子應該走到位置 186。\n"
                + "5. 藍色國王棋子應該走到位置 136。\n"
                + "6. 當國王棋子走到正確的王位時，遊戲結束。";
        JOptionPane.showMessageDialog(frame, gameRules);
    }
}