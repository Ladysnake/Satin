#version 120

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

uniform vec3 CameraPosition;
uniform float STime;

// The magic matrix to get world coordinates from pixel ones
uniform mat4 InverseTransformMatrix;
// The size of the viewport (typically, [0,0,1080,720])
uniform ivec4 ViewPort;


varying vec2 texCoord;
varying vec4 vPosition;

vec4 CalcEyeFromWindow(in float depth)
{
  // derived from https://www.khronos.org/opengl/wiki/Compute_eye_space_from_window_space
  // ndc = Normalized Device Coordinates
  vec3 ndcPos;
  ndcPos.xy = ((2.0 * gl_FragCoord.xy) - (2.0 * ViewPort.xy)) / (ViewPort.zw) - 1;
  ndcPos.z = (2.0 * depth - gl_DepthRange.near - gl_DepthRange.far) / (gl_DepthRange.far - gl_DepthRange.near);
  vec4 clipPos = vec4(ndcPos, 1.);
  vec4 homogeneous = InverseTransformMatrix * clipPos;
  vec4 eyePos = vec4(homogeneous.xyz / homogeneous.w, homogeneous.w);
  return eyePos;
}

void main()
{
    vec4 tex = texture2D(DiffuseSampler, texCoord);

    vec3 ndc = vPosition.xyz / vPosition.w; //perspective divide/normalize
    vec2 viewportCoord = ndc.xy * 0.5 + 0.5; //ndc is -1 to 1 in GL. scale for 0 to 1

    // Depth fading
    float sceneDepth = texture2D(DepthSampler, viewportCoord).x;
    vec3 pixelPosition = CalcEyeFromWindow(sceneDepth).xyz + CameraPosition;
    float d = distance(pixelPosition, CameraPosition);
    d /= mod(STime, 20);
    vec3 lolwat = vec3(sin(STime), cos(STime), sin(STime + 1.)) * smoothstep(2, 3., d) * smoothstep(4, 3., d);
    gl_FragColor = vec4(tex.rgb + pow(lolwat, vec3(3)), 1.);
}
