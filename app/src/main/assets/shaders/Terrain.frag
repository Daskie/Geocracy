#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_loc;
flat in vec3 v2f_norm;
in float v2f_elevation;
in float v2f_super;
in float v2f_sub;
flat in int v2f_coastDist;
in float v2f_border;
in vec3 v2f_edges;
in vec3 v2f_bary;
flat in vec3 v2f_continentColor;
flat in float v2f_selected;
flat in float v2f_highlighted;

layout (location = 0) out vec4 out_color;

uniform vec3 u_lightDir;
uniform float u_time;

const float k_pi = 3.14159265f;
const float k_ambience = 0.15f;
const float k_borderThreshold = 0.875f;
const float k_highlightBorderHighThreshold = 0.75f;
const float k_highlightBorderLowThreshold = 0.5f;
const vec3 k_beachColor = vec3(1.0f, 0.9f, 0.8f);
const vec3 k_rockColor = vec3(0.5f);

float between(float v, float low, float high) {
    return float(v >= low && v <= high);
}

void main() {
    vec3 up = normalize(v2f_loc);
    float t = cos(u_time * 2.0f * k_pi) * -0.5f + 0.5f;

    float land = float(v2f_coastDist > 0);
    float coast = float(v2f_coastDist == 0);
    float ocean = float(v2f_coastDist < 0);

    // Diffuse lighting
    float diffuse = (1.0f - k_ambience) * max(dot(v2f_norm, -u_lightDir), 0.0f) + k_ambience;

    vec3 landColor = v2f_continentColor;
    vec3 coastColor = k_beachColor;
    vec3 oceanColor = coastColor * (1.0f - v2f_sub);

    vec3 albedo =
        land * landColor +
        coast * coastColor +
        ocean * oceanColor;
    albedo = mix(vec3(0.5f), albedo, dot(v2f_norm, up));

    float selectedOrHighlighted = max(v2f_selected, v2f_highlighted);
    float shTime = mix(t, 1.0f, v2f_selected);

    float borderThreshold = mix(k_borderThreshold, mix(k_highlightBorderHighThreshold, k_highlightBorderLowThreshold, shTime), selectedOrHighlighted);
    float maxBary = max(max(v2f_bary.x, v2f_bary.y), v2f_bary.z);
    float maxEdge = max(max(v2f_edges.x, v2f_edges.y), v2f_edges.z);
    float corner = step(borderThreshold, maxBary);
    float edge = step(borderThreshold, maxEdge);
    float border = step(borderThreshold, v2f_border) * (max(corner, edge));
    vec3 borderColor = mix(v2f_continentColor * 0.5f, vec3(1.0f), selectedOrHighlighted);

    float band = between(v2f_border, borderThreshold + (1.0f - k_borderThreshold), k_borderThreshold) * selectedOrHighlighted;
    borderColor = mix(borderColor, vec3(1.0f, 0.0f, 0.0f), band);

    out_color.rgb = mix(
        albedo * (diffuse + (0.125 + shTime * 0.125f) * selectedOrHighlighted * land),
        borderColor * mix(diffuse, 1.0f, selectedOrHighlighted),
        border * land
    );
    out_color.a = 1.0f;
}
