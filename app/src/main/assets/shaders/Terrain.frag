#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_loc;
in vec3 v2f_norm;
in float v2f_elevation;
in float v2f_super;
in float v2f_sub;
flat in int v2f_coastDist;
flat in int v2f_territory;
flat in int v2f_continent;
in float v2f_border;
in vec3 v2f_edges;
in vec3 v2f_bary;

layout (location = 0) out vec4 out_color;
layout (location = 1) out int out_id;

uniform vec3 u_lightDir;
uniform float u_time;
uniform vec3 u_continentColors[16];
uniform int u_selectedTerritory;
uniform int u_highlightedTerritoriesLower;
uniform int u_highlightedTerritoriesUpper;
uniform int u_maxCoastDist;

const float k_pi = 3.14159265f;
const float k_ambience = 0.15f;
const float k_borderThreshold = 0.9f;
const vec3 k_beachColor = vec3(1.0f, 0.9f, 0.8f);

void main() {
    vec3 norm = normalize(v2f_norm);
    float t = cos(u_time * 2.0f * k_pi) * -0.5f + 0.5f;

    float land = float(v2f_coastDist > 0);
    float coast = float(v2f_coastDist == 0);
    float ocean = float(v2f_coastDist < 0);

    // Diffuse lighting
    float diffuse = (1.0f - k_ambience) * max(dot(norm, -u_lightDir), 0.0f) + k_ambience;

    vec3 continentColor = u_continentColors[v2f_continent];
    vec3 landColor = continentColor;
    vec3 coastColor = k_beachColor;
    vec3 oceanColor = vec3(1.0f - v2f_sub);

    vec3 albedo =
        land * landColor +
        coast * coastColor +
        ocean * oceanColor;

    float selected = float(v2f_territory == u_selectedTerritory);

    int highlightedTerritories = v2f_territory < 32 ? u_highlightedTerritoriesLower : u_highlightedTerritoriesUpper;
    float highlighted = float((highlightedTerritories >> (v2f_territory & 0x1F)) & 1);

    float selectedOrHighlighted = max(selected, highlighted);
    float shTime = mix(t, 1.0f, selected);

    float borderThreshold = mix(k_borderThreshold, mix(k_borderThreshold, 0.5f, shTime), selectedOrHighlighted);
    float maxBary = max(max(v2f_bary.x, v2f_bary.y), v2f_bary.z);
    float maxEdge = max(max(v2f_edges.x, v2f_edges.y), v2f_edges.z);
    float corner = step(borderThreshold, maxBary);
    float edge = step(borderThreshold, maxEdge);
    float border = step(borderThreshold, v2f_border) * (max(corner, edge));
    vec3 borderColor = mix(continentColor * 0.5f, continentColor + shTime, selectedOrHighlighted);

    out_color.rgb = mix(albedo * (diffuse + (0.25 + shTime * 0.25f) * selectedOrHighlighted * land), borderColor * mix(diffuse, 1.0f, selectedOrHighlighted), border * land);
    out_color.a = 1.0f;

    out_id = v2f_territory;
}
