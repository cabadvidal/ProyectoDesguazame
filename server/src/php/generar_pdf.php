<?php
require_once __DIR__ . '/lib/fpdf/fpdf.php';

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');
class PDF extends FPDF
{
    private $id_factura;
    private $datos_cliente;
    private $fecha;
    private $datos_empresa;

    // Constructor para inicializar el número de factura
    public function __construct($id_factura, $array_datos_cliente, $fecha, $array_datos_empresa)
    {   // Asignar el valores
        parent::__construct();
        $this->id_factura = $id_factura;
        $this->datos_cliente = $array_datos_cliente;
        $this->fecha = $fecha;
        $this->datos_empresa = $array_datos_empresa;
        //  $this->lineas =$array_lineas;
    }
    // Encabezado
    function Header()
    {
        // Logo de la empresa (ajusta la ruta a la ubicación de tu logo)
        $this->Image('../../public/web/img/menu/coche-transparente.png', 75, 30, 60);
        // Arial bold 15 para el título
        $this->SetFont('Arial', 'B', 14);

        // Mover a la derecha
        //$this->Cell(80);
        // Título de la empresa (convertir UTF-8 a ISO-8859-1 usando mb_convert_encoding)
        $this->Cell(70);
        $this->Cell(100, 8, mb_convert_encoding($this->datos_empresa['RAZON_SOCIAL'], 'ISO-8859-1', 'UTF-8'), 0, 1, 'L');

        // Dirección de la empresa
        $this->SetFont('Arial', '', size: 10);
        $this->Cell(100, 8, mb_convert_encoding($this->datos_empresa['DIRECCION'], 'ISO-8859-1', 'UTF-8'), 0, 1, 'L');
        $this->Cell(100, 8, mb_convert_encoding($this->datos_empresa['CIUDAD'] . ', ' . $this->datos_empresa['MUNICIPIO'] . ' CP-' . $this->datos_empresa['CODIGO_POSTAL'], 'ISO-8859-1', 'UTF-8'), 0, 1, 'L');
        $this->Cell(100, 8, mb_convert_encoding($this->datos_empresa['DNI_CIF'], 'ISO-8859-1', 'UTF-8'), 0, 1, 'L');
        $this->Cell(100, 8, mb_convert_encoding($this->datos_empresa['MOVIL'], 'ISO-8859-1', 'UTF-8'), 0, 1, 'L');
        $this->Cell(100, 8, mb_convert_encoding($this->datos_empresa['MAIL'], 'ISO-8859-1', 'UTF-8'), 0, 1, 'L');

        // Datos del cliente alineados a la derecha
        $this->SetY(18);  // Establecer la misma altura que los datos de la empresa
        foreach ($this->datos_cliente as $campo => $valor) {
            $this->SetX(145);  // Mover a la derecha para alineación
            $this->Cell(100, 8, mb_convert_encoding(/*ucfirst($campo) . ": ".*/ $valor, 'ISO-8859-1', 'UTF-8'), 0, 1, 'L');
        }

        // Espacio
        $this->Ln(10);

        // Factura información
        $this->SetFont('Arial', 'B', 12);
        $this->Cell(100, 10, mb_convert_encoding('Factura Nro: ' . $this->id_factura, 'ISO-8859-1', 'UTF-8'), 0, 0, 'L');
        $this->Cell(90, 10, 'Fecha: ' . $this->fecha, 0, 1, 'R');

        // Línea de separación
        $this->Ln(5);
        $this->Cell(190, 0, '', 'T'); // Dibuja una línea
        $this->Ln(5);
    }

    // Pie de página
    function Footer()
    {
        // Posición a 1.5 cm del final
        $this->SetY(-15);
        // Arial italic 8
        $this->SetFont('Arial', 'I', 8);
        // Número de página
        $this->Cell(0, 10, mb_convert_encoding('Gracias por pagar ! ! !', 'ISO-8859-1', 'UTF-8') /*. $this->PageNo()*/ , 0, 0, 'C');
    }

    /**
     * Función que genera el cuerpo del pdf
     * @param mixed $array_lineas
     * @param mixed $linea_final
     * @return void
     */
    function Body($array_lineas, $linea_final)
    {
        // Definimos el número de líneas por página
        $lineas_por_pagina = 15;
        $total_lineas = count($array_lineas);
        $margen_derecha = 15;

        // Iteramos a través de todas las líneas
        for ($i = 0; $i < $total_lineas; $i++) {
            // Si estamos al inicio de una página, imprimimos el encabezado de la tabla
            if ($i % $lineas_por_pagina == 0) {
                // Si no es la primera página, añadimos una nueva
                if ($i > 0) {
                    $this->AddPage();
                }

                $this->SetFont('Arial', 'B', 12);

                // Encabezado de la tabla
                $this->Ln(5);
                // Mover el encabezado hacia la derecha
                $this->SetX($margen_derecha);
                $this->Cell(30, 10, 'Referencia', 1, 0, 'C');
                $this->Cell(55, 10, mb_convert_encoding('Descripción', 'ISO-8859-1', 'UTF-8'), 1, 0, 'C');
                $this->Cell(25, 10, 'Peso', 1, 0, 'C');
                $this->Cell(25, 10, 'Precio', 1, 0, 'C');
                $this->Cell(50, 10, 'Vendedor', 1, 0, 'C');
                $this->Ln();
            }
            $this->SetFont('Arial', '', 8);
            $this->SetX($margen_derecha);
            // Imprimimos la línea del producto
            $linea = $array_lineas[$i];
            $this->Cell(30, 10, mb_convert_encoding($linea['REFERENCIA'], 'ISO-8859-1', 'UTF-8'), 1, 0, 'C');
            $this->Cell(55, 10, mb_convert_encoding($linea['DESCRIPCION'], 'ISO-8859-1', 'UTF-8'), 1, 0, 'C');
            $this->Cell(25, 10, mb_convert_encoding($linea['PESO'] . ' KG', 'CP1252'), 1, 0, 'C');
            $this->Cell(25, 10, mb_convert_encoding($linea['PRECIO'] . ' €', 'CP1252'), 1, 0, 'C');
            $this->Cell(50, 10, mb_convert_encoding($linea['RAZON_SOCIAL'], 'ISO-8859-1', 'UTF-8'), 1, 0, 'C');
            $this->Ln();
        }

        // Solo imprimimos la línea final (Base, IVA, Total) en la última página
        if ($total_lineas % $lineas_por_pagina != 0 || $total_lineas % $lineas_por_pagina == 0) {
            $this->SetFont('Arial', 'B', 12);

            // Nos aseguramos de que estamos en la última página antes de imprimir el resumen
            $this->SetY(-37);
            $this->SetX($margen_derecha);
            $this->Cell(60, 10, mb_convert_encoding('Base: ' . $linea_final[0]['BASE'] . ' €', 'CP1252'), 1, 0, 'C');
            $this->Cell(60, 10, mb_convert_encoding('IVA: ' . $linea_final[0]['IVA'] . ' €', 'CP1252'), 1, 0, 'C');
            $this->Cell(60, 10, mb_convert_encoding('Total: ' . $linea_final[0]['TOTAL'] . ' €', 'CP1252'), 1, 0, 'C');
            $this->Ln();
        }
    }
}

// Leer entrada JSON del cuerpo del POST
$input = json_decode(file_get_contents('php://input'), true);
if (!$input) {
    http_response_code(400);
    echo json_encode(["status" => "1"]);
    exit;
}

$id_factura = $input['id_factura'];
$piezas = $input['piezas'];
$factura = $input['factura'];
$cliente = $input['cliente'][0];
$empresa = $input['empresa']; 

// Crear el PDF
$pdf = new PDF($id_factura, $cliente, $factura[0]['FECHA'], $empresa);
$pdf->AddPage();
$pdf->Body($piezas, [['BASE' => $factura[0]['BASE'], 'IVA' => $factura[0]['IVA'], 'TOTAL' => $factura[0]['PRECIO_TOTAL']]]);

// Ruta donde se guardará el PDF (ajusta según tu estructura de carpetas)
$ruta_guardado =__DIR__ . '/facturas_generadas' . "/factura_$id_factura.pdf";

// Crear el directorio si no existe
if (!is_dir(dirname($ruta_guardado))) {
    mkdir(dirname($ruta_guardado), 0777, true);
}

// Guardar el PDF en el servidor
$guardado = $pdf->Output('F', $ruta_guardado);

echo json_encode(["status" => "0"]);
exit;


