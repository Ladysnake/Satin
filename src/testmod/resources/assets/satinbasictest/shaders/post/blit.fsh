#version 150

uniform sampler2D DiffuseSampler;

uniform vec4 ColorModulate;
uniform int Multiplier; // just checking ints work

in vec2 texCoord;

out vec4 fragColor;

void main() {

    fragColor = texture(DiffuseSampler, texCoord) * ColorModulate * float(Multiplier);

}