#version 300 es

layout (location = 0) in vec3 in_loc;
layout (location = 1) in vec3 in_norm;
layout (location = 2) in int in_info;

flat out lowp uint v2f_territory;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;

void main() {
    v2f_territory = uint((in_info >> 8) & 0xFF);

    if (dot(in_loc, in_loc) < 1.0f) v2f_territory = uint(0);

	gl_Position = u_projMat * u_viewMat * vec4(in_loc, 1.0f);
}
