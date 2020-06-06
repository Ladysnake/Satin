// a vertex shader that does not require any context to be passed through uniforms
#version 120

varying vec2 texCoord;
varying vec4 vPosition;

void main(){
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vPosition = gl_Position;
    texCoord = vec2(gl_MultiTexCoord0);
}
