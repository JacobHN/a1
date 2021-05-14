#version 430

uniform int colorType;
in vec4 varyingColor;

out vec4 color;

void main(void){
    if(colorType == 1){
        color = varyingColor;
    }else{
        color = vec4(0.0, 0.0, 1.0, 1.0);
    }
}
