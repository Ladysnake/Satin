#version 110

uniform sampler2D DiffuseSampler;

// Time in seconds (+ tick delta)
uniform float STime;

varying vec2 texCoord;

void main() {
    float shift = texCoord.y * 5.;
    vec3 rainbow = vec3(sin(STime + shift) + 0.2, cos(STime + shift + 0.5) + 0.2, sin(STime + shift + 4.) + 0.2);
    vec4 tex = texture2D(DiffuseSampler, texCoord);
    gl_FragColor = min(tex * vec4(rainbow, 1.) * 3., vec4(1.)) + tex;

}