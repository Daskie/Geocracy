#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_loc;
flat in vec3 v2f_norm;
in float v2f_elevation;
in float v2f_super;
in float v2f_sub;
flat in int v2f_coastDist;
flat in int v2f_faceInlandDist;
in float v2f_vertInlandDist;
in float v2f_border;
in vec3 v2f_edges;
in vec3 v2f_bary;
flat in vec3 v2f_continentColor;
flat in float v2f_selected;
flat in float v2f_highlighted;
flat in vec3 v2f_playerColor;

layout (location = 0) out vec4 out_color;

uniform vec3 u_lightDir;
uniform float u_time;
uniform float u_lowElevationFactor, u_highElevationFactor;

const float k_pi = 3.14159265f;
const float k_ambience = 0.15f;
const vec3 k_sandColor = vec3(1.0f, 0.9f, 0.8f);
const vec3 k_rockColor = vec3(0.5f);
const float k_borderEdgeWidth = 0.125f;
const float k_borderThreshold = 1.0f - k_borderEdgeWidth;
const float k_highlightBorderHighThreshold = 0.75f;
const float k_highlightBorderLowThreshold = 0.5f;
const float k_pulseMinDepth = 1.5f;
const float k_pulseMaxDepth = 3.0f;

float between(float v, float low, float high) {
    return float(v >= low && v <= high);
}

vec3 desaturate(vec3 v, float s) {
    return 1.0f - (1.0f - v) * s;
}

float smoothInterp(float v) {
    return v * v * (3.0f - 2.0f * v);
}

void main() {
    vec3 up = normalize(v2f_loc);
    float pulseTime = smoothInterp((u_time > 0.5f ? 1.0f - u_time : u_time) * 2.0f);
    pulseTime = mix(pulseTime, 1.0f, v2f_selected);

    float super = clamp((v2f_elevation - 1.0f) * u_highElevationFactor, 0.0f, 1.0f);
    float sub = clamp((v2f_elevation - 1.0f) * u_lowElevationFactor, 0.0f, 1.0f);

    float land = float(v2f_coastDist > 0);
    float coast = float(v2f_coastDist == 0);
    float ocean = float(v2f_coastDist < 0);

    // Diffuse lighting
    float diffuse = (1.0f - k_ambience) * max(dot(v2f_norm, -u_lightDir), 0.0f) + k_ambience;

    vec3 landColor = desaturate(v2f_continentColor, 0.25f);
    vec3 coastColor = mix(k_sandColor, v2f_continentColor, sign(super));
    vec3 oceanColor = k_sandColor * (1.0f - sub);

    vec3 albedo =
        land * landColor +
        coast * coastColor +
        ocean * oceanColor;
    albedo = mix(vec3(0.5f), albedo, dot(v2f_norm, up));

    float selectedOrHighlighted = max(v2f_selected, v2f_highlighted);

    float maxBary = max(max(v2f_bary.x, v2f_bary.y), v2f_bary.z);
    float maxEdge = max(max(v2f_edges.x, v2f_edges.y), v2f_edges.z);
    float innerBorderThreshold = mix(k_borderThreshold, mix(k_highlightBorderHighThreshold, k_highlightBorderLowThreshold, pulseTime), selectedOrHighlighted);
    float innerCorner = step(innerBorderThreshold, maxBary);
    float innerEdge = step(innerBorderThreshold, maxEdge);
    float innerBorder = step(innerBorderThreshold, v2f_border) * (max(innerCorner, innerEdge));
    float outerBorderThreshold = innerBorderThreshold * 0.5f + 0.5f;
    float outerCorner = step(outerBorderThreshold, maxBary);
    float outerEdge = step(outerBorderThreshold, maxEdge);
    float outerBorder = step(outerBorderThreshold, v2f_border) * (max(outerCorner, outerEdge));
    vec3 borderColor = mix(v2f_continentColor * 0.75f, v2f_playerColor + outerBorder, selectedOrHighlighted);

    /*float pulseWidth = 5.0f;
    float pulseDepth = 12.0f;
    float pulseAt = u_time * (pulseDepth + 2.0f * pulseWidth) - pulseWidth;
    float pulseDist = abs(float(v2f_inlandDist) - pulseAt);
    float pulseValue = max(1.0f - pulseDist / pulseWidth, 0.0f) * (1.0f - u_time * u_time);
    pulseValue = max(1.0f - float(v2f_inlandDist) * 0.25f, 0.0f);*/
    float pulseAt = mix(k_pulseMinDepth, k_pulseMaxDepth, pulseTime);
    float pulseValue = max(1.0f - float(v2f_vertInlandDist) / pulseAt, 0.0f);
    pulseValue *= pulseValue;
    albedo *= 1.0f + 0.125 * selectedOrHighlighted * land;
    albedo += v2f_playerColor * (pulseValue * selectedOrHighlighted * land);

    out_color.rgb = mix(
        albedo * diffuse,
        borderColor * mix(diffuse, 1.0f, selectedOrHighlighted),
        innerBorder * land
    );
    out_color.a = 1.0f;
}
