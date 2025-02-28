#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform sampler2D Sampler2;

uniform float STime;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;
in vec2 texCoord2;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a < vertexColor.a) {
        discard;
    }

    float shift = texCoord0.y * 5.;
    vec3 rainbow = vec3(sin(STime + shift) + 0.2, cos(STime + shift + 0.5) + 0.2, sin(STime + shift + 4.) + 0.2);
    fragColor = min(color * vec4(rainbow, 1.) * 3., vec4(1.)) + color;
}
