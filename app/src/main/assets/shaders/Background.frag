#version 300 es
precision highp float;
in vec2 v2f_loc;

out vec4 out_color;

uniform mat4 u_projMat;
uniform mat4 u_viewMat;
uniform float u_time;
uniform float u_collision;

const float k_pi = 3.14159265f;
const vec3 k_skyLight = vec3(0.5f, 0.75f, 0.9f);
const vec3 k_skyDark = vec3(0.2f, 0.2f, 0.5f);
const vec3 k_sunColor = vec3(1.0f, 1.0f, 0.9f);
const vec3 k_moonColor = vec3(0.9f, 0.9f, 1.0f);

void main() {
	mat4 invProj = inverse(u_projMat);
	vec4 temp1 = invProj * vec4(v2f_loc.x, v2f_loc.y, 1.0f, 1.0f);
	vec4 temp2 = invProj * vec4(v2f_loc.x, v2f_loc.y, 0.0f, 1.0f);
	vec3 ray = normalize(temp1.xyz / temp1.w - temp2.xyz / temp2.w);
	ray = normalize(inverse(mat3(u_viewMat)) * ray);

	vec3 sun = vec3(cos(u_time), sin(u_time), 0.0f);

	float horizonIntensity = 1.0f - abs(dot(ray, vec3(0.0f, 1.0f, 0.0f)));

	float sunIntensity = dot(sun, ray) * 0.5f + 0.5f;
	float moonIntensity = dot(-sun, ray) * 0.5f + 0.5f;

	vec3 skyColor = mix(k_skyDark, k_skyLight, pow(horizonIntensity, 4.0f)) * (sun.y * 0.45f + 0.55f);
	vec3 horizonColor = vec3(pow(horizonIntensity, 4.0f), 0.0f, 0.0f);
	horizonColor *= 1.0f - abs(sun.y);
	vec3 sunColor = k_sunColor * clamp((sunIntensity - 0.995f) / 0.0005f, 0.0f, 1.0f);
	vec3 moonColor = k_moonColor * clamp((moonIntensity - 0.9995f) / 0.00005f, 0.0f, 1.0f);

	out_color = vec4(skyColor + horizonColor + sunColor + moonColor, 1.0f);

	// collision

	out_color.rgb += u_collision;
}