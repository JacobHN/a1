#version 430

uniform float xRad;
uniform float yRad;
uniform float size;

out vec4 varyingColor;

void main(void){
  if (gl_VertexID == 0){
    gl_Position = vec4(0.1 * size + xRad,-0.3 * size + yRad, 0.0, 1.0);
    varyingColor = vec4(1.0,0.0,0.0,1.0);
  }
  else if (gl_VertexID == 1){
    gl_Position = vec4(-0.1 * size + xRad,-0.3 * size + yRad, 0.0, 1.0);
    varyingColor = vec4(0.0,1.0,0.0,1.0);
  }
  else {
    gl_Position = vec4(0.0 * size + xRad, 0.3 * size + yRad, 0.0, 1.0);
    varyingColor = vec4(0.0,0.0,1.0,1.0);
  }
}