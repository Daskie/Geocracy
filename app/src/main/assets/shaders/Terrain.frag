#version 300 es

precision highp float;

in vec3 v2f_loc;
in vec3 v2f_norm;
in float v2f_elevation;
in float v2f_super;
in float v2f_sub;

out vec4 out_color;

uniform vec3 u_lightDir;

const float k_ambience = 0.15f;

vec3 detSuperColor(float super) {
    return vec3(1.0f);
}

vec3 detSubColor(float sub) {
    return vec3(0.0f, 0.0f, 1.0f - sub);
}

void main() {
    vec3 norm = normalize(v2f_norm);

    if (v2f_super > 0.0f && v2f_sub > 0.0f) {
        out_color.rgb = vec3(0.5f);
    }
    else if (v2f_super > 0.0f) {
        out_color.rgb = detSuperColor(v2f_super);
    }
    else {
        out_color.rgb = detSubColor(v2f_sub);
    }
    out_color.a = 1.0f;

    // Diffuse lighting
    float diffuseK = max(dot(norm, u_lightDir), 0.0f) + k_ambience;
    out_color.rgb *= vec3(diffuseK);
}
