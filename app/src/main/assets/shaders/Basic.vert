#version 300 es

layout (location = 0) in vec3 in_loc;
layout (location = 1) in vec3 in_norm;

out vec3 v2f_loc;
out vec3 v2f_norm;

uniform mat4 u_modelMat;
uniform mat3 u_normMat;
uniform mat4 u_viewMat;
uniform mat4 u_projMat;

void main() {
    v2f_loc = (u_modelMat * vec4(in_loc, 1.0f)).xyz;
    v2f_norm = u_normMat * in_norm;

    gl_Position = u_projMat * u_viewMat * vec4(v2f_loc, 1.0f);
}
