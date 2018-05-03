#version 300 es

layout(location = 0) in vec2 in_pos;
layout(location = 1) in float in_angle;
layout(location = 2) in mat3 in_basis;

out vec3 v2f_loc;
out vec3 v2f_norm;
out vec2 v2f_pos;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;

const float k_elevation = 1.0001f;
const float k_thickness = 0.01f;

void main() {
    float theta = in_pos.x * in_angle;
    vec2 p = vec2(cos(theta), sin(theta));
    v2f_loc = in_basis * (vec3(p, in_pos.y * k_thickness) * k_elevation);
    v2f_norm = in_basis * vec3(p, 0.0f);
    v2f_pos = in_pos;

    gl_Position = u_projMat * u_viewMat * vec4(v2f_loc, 1.0f);
}
