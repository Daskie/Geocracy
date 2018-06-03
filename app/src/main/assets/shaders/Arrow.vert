#version 300 es

layout(location = 0) in vec2 in_pos;

out vec2 v2f_pos;
flat out float v2f_angle;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;
uniform float u_angle;
uniform mat3 u_basis;

const float k_elevation = 1.01f;
const float k_thickness = 0.025f;

void main() {
    float theta = in_pos.x * u_angle;
    vec2 p = vec2(cos(theta), sin(theta));
    vec3 loc = u_basis * (vec3(p, in_pos.y * k_thickness) * k_elevation);

    v2f_pos = in_pos;
    v2f_angle = u_angle;

    gl_Position = u_projMat * u_viewMat * vec4(loc, 1.0f);
}
