#version 300 es

layout(location = 0) in vec2 in_pos;
layout(location = 1) in int in_info;
layout(location = 2) in float in_angle;
layout(location = 3) in mat3 in_basis;

out vec3 v2f_loc;
out vec3 v2f_norm;
out vec2 v2f_pos;
flat out float v2f_selected;
flat out vec3 v2f_continentColor;
flat out float v2f_angle;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;
uniform int u_selectedTerritory;
uniform vec3 u_continentColors[16];

const float k_elevation = 1.0001f;
const float k_thickness = 0.0025f;

void main() {
    int territory = in_info & 0xFFFF;
    v2f_selected = float(territory == u_selectedTerritory);
    int continent = (in_info >> 16) & 0xFFFF;
    v2f_continentColor = u_continentColors[continent];

    float theta = in_pos.x * in_angle;
    vec2 p = vec2(cos(theta), sin(theta));
    v2f_loc = in_basis * (vec3(p, in_pos.y * k_thickness * (1.0f + v2f_selected)) * k_elevation);
    v2f_norm = in_basis * vec3(p, 0.0f);
    v2f_pos = in_pos;

    v2f_angle = in_angle;

    gl_Position = u_projMat * u_viewMat * vec4(v2f_loc, 1.0f);
}
