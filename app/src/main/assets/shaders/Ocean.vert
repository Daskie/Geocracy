#version 300 es

layout(location = 0) in vec3 in_loc;
layout(location = 1) in vec3 in_norm;

out vec3 v2f_loc;
out vec3 v2f_norm;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;

void main() {
    v2f_loc = in_loc;// * 1.0001f;
    v2f_norm = in_norm;

    gl_Position = u_projMat * u_viewMat * vec4(v2f_loc, 1.0f);
}
