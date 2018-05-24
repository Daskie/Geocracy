#version 300 es

layout (location = 0) in vec3 in_loc;
layout (location = 1) in vec3 in_norm;
layout (location = 2) in vec3 in_armyLocation;
layout (location = 3) in int in_playerId;

out vec3 v2f_loc;
out vec3 v2f_norm;
flat out vec3 v2f_playerColor;
out vec3 v2f_worldNorm;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;
uniform vec3 u_playerColors[9];

const float k_scale = 0.015f;

void main() {
    v2f_loc = k_scale * in_loc + in_armyLocation;
    v2f_norm = in_norm;
    v2f_playerColor = u_playerColors[in_playerId];
    v2f_worldNorm = normalize(v2f_loc);

    gl_Position = u_projMat * u_viewMat * vec4(v2f_loc, 1.0f);
}
