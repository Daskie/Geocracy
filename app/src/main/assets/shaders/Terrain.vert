#version 300 es

layout (location = 0) in vec3 in_loc;
layout (location = 1) in vec3 in_norm;
layout (location = 2) in int in_info;

out vec3 v2f_loc;
flat out vec3 v2f_norm;
out float v2f_elevation;
out float v2f_super;
out float v2f_sub;
flat out int v2f_coastDist;
out float v2f_border;
out vec3 v2f_edges;
out vec3 v2f_bary;
flat out vec3 v2f_continentColor;
flat out float v2f_selected;
flat out float v2f_highlighted;

uniform mat4 u_viewMat;
uniform mat4 u_projMat;
uniform float u_lowElevation, u_highElevation;
uniform vec3 u_continentColors[16];
uniform int u_selectedTerritory;
uniform int u_highlightedTerritoriesLower;
uniform int u_highlightedTerritoriesUpper;

void main() {
    v2f_loc = in_loc;
    v2f_norm = in_norm;

    v2f_elevation = length(in_loc);

    v2f_super = clamp((v2f_elevation - 1.0f) / (u_highElevation - 1.0f), 0.0f, 1.0f);
    v2f_sub = clamp((v2f_elevation - 1.0f) / (u_lowElevation - 1.0f), 0.0f, 1.0f);

    v2f_coastDist = in_info & 0xFF;
    if (v2f_coastDist >= 128) v2f_coastDist -= 256; // sign extend
    int territory = (in_info >> 8) & 0xFF;
    int continent = (in_info >> 16) & 0xF;
    v2f_border = float((in_info >> 20) & 1);
    v2f_edges = vec3(float((in_info >> 21) & 1), float((in_info >> 22) & 1), float((in_info >> 23) & 1));

    int mod3 = gl_VertexID % 3;
    v2f_bary = vec3(mod3 == 0, mod3 == 1, mod3 == 2);

    v2f_continentColor = u_continentColors[continent];

    v2f_selected = float(territory == u_selectedTerritory);

    int highlightedTerritories = territory < 32 ? u_highlightedTerritoriesLower : u_highlightedTerritoriesUpper;
    v2f_highlighted = float((highlightedTerritories >> (territory & 0x1F)) & 1);

	gl_Position = u_projMat * u_viewMat * vec4(in_loc, 1.0f);
}
