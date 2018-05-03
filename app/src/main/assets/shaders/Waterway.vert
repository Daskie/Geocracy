#version 300 es

layout(location = 0) in vec2 in_loc;
layout(location = 1) in float in_angle;
layout(location = 2) in mat3 in_basis;

out vec3 v2f_loc;
out vec3 v2f_norm;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;

const float k_elevation = 1.0001f;
const float k_thickness = 0.0025f;

void main() {
    float theta = in_loc.x * in_angle;
    vec2 p = vec2(cos(theta), sin(theta));
    v2f_loc = in_basis * (vec3(p, in_loc.y * k_thickness) * k_elevation);
    v2f_norm = in_basis * vec3(p, 0.0f);

    gl_Position = u_projMat * u_viewMat * vec4(v2f_loc, 1.0f);
}
