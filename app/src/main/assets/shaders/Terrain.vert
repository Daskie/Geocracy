#version 300 es

layout(location = 0) in vec3 in_loc;
layout(location = 1) in vec3 in_norm;
layout(location = 2) in int in_identity;

out vec3 v2f_loc;
out vec3 v2f_norm;
out float v2f_elevation;
out float v2f_super;
out float v2f_sub;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;
uniform float u_lowElevation, u_highElevation;

void main() {
    v2f_loc = in_loc;
    v2f_norm = in_norm;

    v2f_elevation = length(in_loc);

    v2f_super = clamp((v2f_elevation - 1.0f) / (u_highElevation - 1.0f), 0.0f, 1.0f);
    v2f_sub = clamp((v2f_elevation - 1.0f) / (u_lowElevation - 1.0f), 0.0f, 1.0f);

	gl_Position = u_projMat * u_viewMat * vec4(in_loc, 1.0f);
}
