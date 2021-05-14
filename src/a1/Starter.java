/**
 * Creates a triangle with vertical motion, circular motion and ability to change size and color
 *
 * @author Jacob Hua
 * @version 1.0
 * @since 2020-02-08
 *
 */
package a1;

import javax.swing.*;

import static com.jogamp.opengl.GL4.*;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import java.awt.*;
import java.awt.event.*;


public class Starter extends JFrame implements GLEventListener, KeyListener, MouseWheelListener {

    private GLCanvas myCanvas;
    private int renderingProgram;
    private int vao[] = new int[1];

    //color variables
    private final int MONO_COLOR = 0;
    private final int VARYING_COLOR = 1;
    private int colorType = 0;

    //animation variables
    private final int NO_ANIMATION = 0;
    private final int VERTICAL_ANIMATION = 1;
    private final int CIRCULAR_ANIMATION = 2;
    private int animation = 0;

    //variables for vertical and circular animation
    private float increment = 0.01f;
    private float y = 0.0f;
    private float x = 0.0f;
    private float xRad = 0;
    private float yRad= 0;
    private double currentRadian = 0;

    //variables for increasing/decreasing triangle size
    private float size = 1.0f;

    //variables for timing animations
    private double startTime;
    private double elapsedTime;
    private double currentTime;

    //Top bar
    JPanel topBar;

    //Buttons
    JButton verButton;
    JButton cirButton;
    JButton colButton;

    /**
     * Constructor
     */
    public Starter() {
        setTitle("assignment 1");
        setSize(600,400);

        this.setLayout(new BorderLayout());

        //top bar
        topBar = new JPanel();

        //vertical button
        verButton = new JButton("Vertical");
        verButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                VerticalMotion();
            }
        });
        topBar.add(verButton);

        //circle button
        cirButton = new JButton("Circle");
        cirButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                CircularMotion();
            }
        });
        topBar.add(cirButton);

        //color button
        colButton = new JButton("Color");
        colButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ChangeColorType();
            }
        });
        topBar.add(colButton);
        this.add(topBar, BorderLayout.NORTH);

        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        myCanvas.addKeyListener(this);
        myCanvas.addMouseWheelListener(this);
        this.add(myCanvas);

        this.setVisible(true);

        Animator animator = new Animator(myCanvas);
        animator.start();
    }

    /**
     * displays a triangle
     * @param drawable
     */
    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();
        gl.glClear(GL_COLOR_BUFFER_BIT);
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glUseProgram(renderingProgram);

        //calculates time when display is called
        elapsedTime = System.currentTimeMillis() - startTime;
        //creates vertical motion by using sin
        if(animation == VERTICAL_ANIMATION) {
            currentRadian += (elapsedTime - currentTime)/1000;
            yRad = (float)Math.sin(currentRadian) * 1.0f;
            int currentY = gl.glGetUniformLocation(renderingProgram, "yRad");
            gl.glProgramUniform1f(renderingProgram, currentY, yRad);
            Utils.printProgramLog(renderingProgram);

        //creates circular motion by using sin and cos
        }else if(animation == CIRCULAR_ANIMATION){
            currentRadian += (elapsedTime - currentTime)/1000;
            xRad = (float)Math.cos(currentRadian) * 1.0f;
            int currentX = gl.glGetUniformLocation(renderingProgram, "xRad");
            yRad = (float)Math.sin(currentRadian) * 1.0f;
            int currentY = gl.glGetUniformLocation(renderingProgram, "yRad");
            gl.glProgramUniform1f(renderingProgram, currentX, xRad);
            gl.glProgramUniform1f(renderingProgram, currentY, yRad);

        //reverts the triangle to the center in a stationary position
        }else{
            startTime = System.currentTimeMillis();
            currentRadian = 0;
            y = 0.0f;
            x = 0.0f;
            int xLocation = gl.glGetUniformLocation(renderingProgram, "xRad");
            gl.glProgramUniform1f(renderingProgram, xLocation, x);
            int yLocation = gl.glGetUniformLocation(renderingProgram, "yRad");
            gl.glProgramUniform1f(renderingProgram, yLocation, y);
        }

        //change color to current type
        int vertColorType = gl.glGetUniformLocation(renderingProgram, "colorType");
        gl.glProgramUniform1i(renderingProgram, vertColorType, colorType);

        //change size of triangle by mouse wheel
        int currentSize = gl.glGetUniformLocation(renderingProgram, "size");
        gl.glProgramUniform1f(renderingProgram, currentSize, size);

        //calculates when the last time display was called and stores it
        currentTime = System.currentTimeMillis() - startTime;

        gl.glDrawArrays(GL_TRIANGLES, 0, 3);

        Utils.checkOpenGLError();
        Utils.printProgramLog(renderingProgram);
    }

    public static void main(String[] args) {
        new Starter();
    }

    /**
     * Initializes the program
     * @param drawable
     */
    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) GLContext.getCurrentGL();

        startTime = System.currentTimeMillis();

        //print out system OpenGL, JOGL and JAVA
        System.out.println("OpenGL version: " +  gl.glGetString(GL_VERSION));
        System.out.println("JOGL version: " + Package.getPackage("com.jogamp.opengl").getImplementationVersion());
        System.out.println("Java version: " + System.getProperty("java.version"));

        //reads glsl files using Professor Gordon's given Utils java file
        renderingProgram = Utils.createShaderProgram("a1/vertShader.glsl", "a1/fragShader.glsl");
        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
    public void dispose(GLAutoDrawable drawable) {}

    /**
     * implementation of inteface KeyListener
     * @param keyEvent key pressed
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch(keyEvent.getKeyCode()){
            //key push a, change to vertical animation
            case KeyEvent.VK_A:
                VerticalMotion();
                break;
            //key push s, change to circular animation
            case KeyEvent.VK_S:
                CircularMotion();
                break;
            //key push d, change between mono color or varying color
            case KeyEvent.VK_D:
                ChangeColorType();
                break;
        }
    }
    @Override
    public void keyReleased(KeyEvent keyEvent) {}
    @Override
    public void keyTyped(KeyEvent keyEvent) {}

    /**
     * implementation of interface MouseWheelListener
     * @param keyEvent mouse wheel scrolling
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent keyEvent) {
        //scroll up
        if(keyEvent.getWheelRotation() == -1){
            size += .1;
        }else if (keyEvent.getWheelRotation() == 1 && size >= 0){
            size -= .1;
        }
    }

    /**
     * sets animation to integer 1 or 0 to be passed to the vertShader stop or depict vertical motion
     */
    public void VerticalMotion(){
        animation = (animation != VERTICAL_ANIMATION) ? VERTICAL_ANIMATION : NO_ANIMATION;
    }

    /**
     * sets animation to integer 2 or 0 to be passed the vertShader to stop or depict circular motion
     */
    public void CircularMotion(){
        animation = (animation != CIRCULAR_ANIMATION) ? CIRCULAR_ANIMATION : NO_ANIMATION;
    }

    /**
     * sets colorType to integer 1 or 0 to be passed into the fragShader to change color type to either mono or
     * a gradient of 3 colors
     */
    public void ChangeColorType(){
        colorType = (colorType != VARYING_COLOR) ? VARYING_COLOR : MONO_COLOR;
    }
}
