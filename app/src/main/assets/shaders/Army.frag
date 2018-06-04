#version 300 es

precision highp float;
precision highp int;

in vec3 v2f_loc;
in vec3 v2f_norm;
flat in vec3 v2f_playerColor;
in vec3 v2f_worldNorm;

out vec4 out_color;

uniform vec3 u_cameraLoc;
uniform vec3 u_lightDir;

const float k_ambience = 0.225f;
const float k_shininess = 16.0f;

void main() {
    vec3 norm = normalize(v2f_norm);
    vec3 view = normalize(u_cameraLoc - v2f_loc);
    vec3 light = -u_lightDir;
    vec3 refl = reflect(-light, norm);

    float diffuse = max(dot(norm, -u_lightDir), 0.0f);
    float specular = pow(max(dot(view, refl), 0.0f), k_shininess);
    float lightFactor = min(diffuse * 0.75f + specular * 0.5f + k_ambience, 1.0f);

    float shadeFactor = min(dot(normalize(v2f_worldNorm), light) * 2.0f, 1.0f); // substitute for being in shadow of planet

    out_color.rgb = v2f_playerColor * max(lightFactor * shadeFactor, k_ambience);
    out_color.a = 1.0f;
}
