#version 300 es

layout(location = 0) in vec2 in_loc;

out vec3 v2f_loc;
out vec3 v2f_norm;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;

void main() {
    v2f_loc = vec3(in_loc, 0.0f);
    v2f_norm = vec3(0.0f, 0.0f, 1.0f);

    gl_Position = u_projMat * u_viewMat * vec4(v2f_loc, 1.0f);
}
