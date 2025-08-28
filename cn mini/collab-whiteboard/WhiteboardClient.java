import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
public class WhiteboardClient extends JFrame {
        private ObjectOutputStream out;
        private DrawArea drawArea;
        private String mode = "DRAW"; 
    
        public WhiteboardClient(String serverAddress) {
            setTitle("Collaborative Whiteboard");
            setSize(800, 600);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
    
            drawArea = new DrawArea();
            add(drawArea, BorderLayout.CENTER);
    
            JPanel buttonPanel = new JPanel();
    
            JButton drawBtn = new JButton("Draw");
            drawBtn.addActionListener(e -> mode = "DRAW");
    
            JButton eraseBtn = new JButton("Erase");
            eraseBtn.addActionListener(e -> mode = "ERASE");
    
            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(e -> {
                try {
                    out.writeObject("CLEAR");
                    out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
    
            buttonPanel.add(drawBtn);
            buttonPanel.add(eraseBtn);
            buttonPanel.add(clearBtn);
    
            add(buttonPanel, BorderLayout.SOUTH);
    
            try {
                Socket socket = new Socket(serverAddress, 12345);
                out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
    
                new Thread(() -> {
                    try {
                        Object obj;
                        while ((obj = in.readObject()) != null) {
                            if (obj.equals("CLEAR")) {
                                drawArea.clear();
                            } else {
                                drawArea.addPoint((PointData) obj);
                            }
                            drawArea.repaint();
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }).start();
    
            } catch (IOException e) {
                e.printStackTrace();
            }
    
            setVisible(true);
        }
    
        class DrawArea extends JPanel {
            private java.util.List<PointData> points = new java.util.ArrayList<>();
    
            public DrawArea() {
                addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        sendPoint(e.getX(), e.getY());
                    }
                });
    
                addMouseMotionListener(new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent e) {
                        sendPoint(e.getX(), e.getY());
                    }
                });
            }
    
            private void sendPoint(int x, int y) {
                try {
                    PointData point = new PointData(x, y, mode);
                    points.add(point);
                    out.writeObject(point);
                    out.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                repaint();
            }
    
            public void addPoint(PointData point) {
                points.add(point);
            }
    
            public void clear() {
                points.clear();
            }
    
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (PointData p : points) {
                    if (p.mode.equals("DRAW")) {
                        g.setColor(Color.BLACK);
                        g.fillOval(p.x, p.y, 5, 5);
                    } else if (p.mode.equals("ERASE")) {
                        g.setColor(Color.WHITE);
                        g.fillOval(p.x, p.y, 20, 20);
                    }
                }
            }
        }
    
        static class PointData implements Serializable {
            int x, y;
            String mode;
    
            PointData(int x, int y, String mode) {
                this.x = x;
                this.y = y;
                this.mode = mode;
            }
        }
    
        public static void main(String[] args) {
            new WhiteboardClient("localhost");
        }
}