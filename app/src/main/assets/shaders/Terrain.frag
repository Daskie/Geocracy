#version 300 es

#define MAX_N_CONTINENTS 41

precision highp float;

in vec3 v2f_loc;
in vec3 v2f_norm;
in float v2f_elevation;
in float v2f_super;
in float v2f_sub;
in float v2f_coastDist;
flat in int v2f_territory;
in float v2f_border;
in vec3 v2f_edges;
in vec3 v2f_bary;

out vec4 out_color;

uniform vec3 u_lightDir;
uniform vec3 u_continentColors[MAX_N_CONTINENTS];

const float k_ambience = 0.15f;
const float k_borderThreshold = 0.9f;

void main() {
    vec3 norm = normalize(v2f_norm);

    /*if (v2f_super > 0.0f && v2f_sub > 0.0f) {
        out_color.rgb = vec3(0.5f);
    }
    else if (v2f_super > 0.0f) {
        out_color.rgb = detSuperColor(v2f_super);
    }
    else {
        out_color.rgb = detSubColor(v2f_sub);
    }*/

    vec3 continentColor = u_continentColors[v2f_territory % MAX_N_CONTINENTS];
    vec3 albedo;
    if (v2f_coastDist >= 0.0f) {
        albedo = continentColor;
    }
    else {
        albedo = vec3(0.0f, 0.0f, 1.0f - v2f_sub);
    }
    //vec3 albedo = vec3(max(v2f_coastDist, 0.0f), 0.0f, max(-v2f_coastDist, 0.0f));

    // Diffuse lighting
    float diffuseK = max(dot(norm, u_lightDir), 0.0f) + k_ambience;
    out_color.rgb *= vec3(diffuseK);

    float maxBary = max(max(v2f_bary.x, v2f_bary.y), v2f_bary.z);
    float corner = step(k_borderThreshold, maxBary);
    float edge = step(k_borderThreshold, max(max(v2f_edges.x, v2f_edges.y), v2f_edges.z));
    float border = step(0.0f, v2f_coastDist) * step(k_borderThreshold, v2f_border) * (max(corner, edge));
    out_color.rgb += border * 0.5f;

    //diffuseK = 1.0f;
    out_color.rgb = mix(albedo * diffuseK, (continentColor + 1.0f) * 0.5f, border);
    out_color.a = 1.0f;
}
