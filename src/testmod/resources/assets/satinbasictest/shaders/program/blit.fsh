#version 110

uniform sampler2D DiffuseSampler;

uniform vec4 ColorModulate;
uniform int Multiplier; // just checking ints work

varying vec2 texCoord;

void main() {

    gl_FragColor = texture2D(DiffuseSampler, texCoord) * ColorModulate * float(Multiplier);

}