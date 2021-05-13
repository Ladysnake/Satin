// a vertex shader that does not require any context to be passed through uniforms
#version 150

out vec2 texCoord;
out vec4 vPosition;

void main(){
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    vPosition = gl_Position;
    texCoord = vec2(gl_MultiTexCoord0);
}
