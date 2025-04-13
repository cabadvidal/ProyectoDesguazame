<?php
$pAccion = 46.7;
$capital = 10000;
$pDividendo = 1.61;
$totalAcumulado = 0;
$ahorro = 0;
$beneficio = 0;

for ($i = 0; $i < 10; $i++) {
    $numeroAcciones = $capital / $pAccion;
    $beneficio = $numeroAcciones * $pDividendo;
    print_R('Beneficio de acciones = ' . $beneficio . ' Año ' . $i + 1 . '.' . PHP_EOL);
    $capital += $beneficio;
    $capital += $ahorro;
}

$capital = $capital + ($capital * 0.05) * 3;

print_R('Capital total = ' . $capital);

